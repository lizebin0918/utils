import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ExcelSaxReader extends DefaultHandler {

	private ExcelSaxReader() {
	}

	private static class InnerClass {
		private static final ExcelSaxReader INSTANCE = new ExcelSaxReader();
	}

	public static ExcelSaxReader getInstance() {
		return InnerClass.INSTANCE;
	}

	/**
	 * 共享字符串表
	 */
	private SharedStringsTable sst;

	/**
	 * 上一次的内容
	 */
	private StringBuilder lastContents = new StringBuilder();

	/**
	 * 字符串标识
	 */
	private boolean nextIsString;

	/**
	 * 列值集合
	 */
	private Map<String, String> columnValueMap = new HashMap<>();

	/**
	 * 结果集
	 */
	private List<Map<String, String>> resultList = new LinkedList<>();

	/**
	 * T元素标识
	 */
	private boolean isTElement;

	/**
	 * 当前行
	 */
	private int curRow = 0;

	/**
	 * 单元格数据类型，默认为字符串类型
	 */
	private CELL_DATA_TYPE_ENUM nextDataType = CELL_DATA_TYPE_ENUM.SSTINDEX;

	private short formatIndex;

	private String formatString;

	// 定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
	private String preRef = null, ref = null;

	// 定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格
	private String maxRef = null;

	/**
	 * 单元格
	 */
	private StylesTable stylesTable;

	/**
	 * 遍历工作簿中所有的电子表格，只能串行读取，并行读取数据会有误
	 * @param filename
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws SAXException
	 * @throws Exception
	 */
	public synchronized List<Map<String, String>> process(String filename) throws IOException, OpenXML4JException, SAXException {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader xssfReader = new XSSFReader(pkg);
		stylesTable = xssfReader.getStylesTable();
		SharedStringsTable sst = xssfReader.getSharedStringsTable();
		XMLReader parser = this.fetchSheetParser(sst);
		Iterator<InputStream> sheets = xssfReader.getSheetsData();
		while (sheets.hasNext()) {
			curRow = 0;
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
		return resultList;
	}

	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		this.sst = sst;
		parser.setContentHandler(this);
		return parser;
	}

	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		// c => 单元格
		if ("c".equals(name)) {
			// 前一个单元格的位置
			if (preRef == null) {
				preRef = attributes.getValue("r");
			} else {
				preRef = ref;
			}
			// 当前单元格的位置
			ref = attributes.getValue("r");
			// 设定单元格类型
			this.setNextDataType(attributes);
			// Figure out if the value is an index in the SST
			String cellType = attributes.getValue("t");
			nextIsString = cellType != null && cellType.equals("s");
		}

		// 当元素为t时
		isTElement = "t".equals(name);

		// 置空
		lastContents = new StringBuilder();

	}

	/**
	 * 单元格中的数据可能的数据类型
	 */
	enum CELL_DATA_TYPE_ENUM {
		BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
	}

	/**
	 * 处理数据类型
	 *
	 * @param attributes
	 */
	private void setNextDataType(Attributes attributes) {
		nextDataType = CELL_DATA_TYPE_ENUM.NUMBER;
		formatIndex = -1;
		formatString = null;
		String cellType = attributes.getValue("t");
		String cellStyleStr = attributes.getValue("s");

		if ("b".equals(cellType)) {
			nextDataType = CELL_DATA_TYPE_ENUM.BOOL;
		} else if ("e".equals(cellType)) {
			nextDataType = CELL_DATA_TYPE_ENUM.ERROR;
		} else if ("inlineStr".equals(cellType)) {
			nextDataType = CELL_DATA_TYPE_ENUM.INLINESTR;
		} else if ("s".equals(cellType)) {
			nextDataType = CELL_DATA_TYPE_ENUM.SSTINDEX;
		} else if ("str".equals(cellType)) {
			nextDataType = CELL_DATA_TYPE_ENUM.FORMULA;
		} else if (Objects.isNull(cellType)) {
			nextDataType = CELL_DATA_TYPE_ENUM.NULL;
			return;
		}

		if (cellStyleStr != null) {
			int styleIndex = Integer.parseInt(cellStyleStr);
			XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
			formatIndex = style.getDataFormat();
			formatString = style.getDataFormatString();

			if (Objects.nonNull(formatString) && formatString.startsWith("m/d/yy")) {
				nextDataType = CELL_DATA_TYPE_ENUM.DATE;
				formatString = "yyyy-MM-dd HH:mm:ss";
			}

			if (formatString == null) {
				nextDataType = CELL_DATA_TYPE_ENUM.NULL;
				formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
			}
		}
	}

	/**
	 * 对解析出来的数据进行类型处理
	 *
	 * @param value   单元格的值（这时候是一串数字）
	 * @param thisStr 一个空字符串
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getDataValue(String value, String thisStr) {
		switch (nextDataType) {
			// 这几个的顺序不能随便交换，交换了很可能会导致数据错误
			case BOOL:
				char first = value.charAt(0);
				thisStr = first == '0' ? "FALSE" : "TRUE";
				break;
			case ERROR:
				thisStr = "\"ERROR:" + value + '"';
				break;
			case FORMULA:
				thisStr = '"' + value + '"';
				break;
			case INLINESTR:
				XSSFRichTextString rtsi = new XSSFRichTextString(value);
				thisStr = rtsi.toString();
				break;
			case SSTINDEX:
				try {
					int idx = Integer.parseInt(value);
					XSSFRichTextString rtss = new XSSFRichTextString(sst.getEntryAt(idx));
					thisStr = rtss.toString();
				} catch (Exception ex) {
					thisStr = value;
				}
				break;
			case NUMBER:
				if (formatString != null) {
					thisStr = new DataFormatter().formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
				} else {
					thisStr = value;
				}
				thisStr = thisStr.replace("_", "").trim();
				break;
			case DATE:
				thisStr = new DataFormatter().formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
				break;
			default:
				thisStr = Objects.toString(value, "");
				break;
		}
		return thisStr;
	}

	@Override
	public void endElement(String uri, String localName, String name) {
		//单元格为空的问题
		if("c".equals(name) && nextDataType == CELL_DATA_TYPE_ENUM.NULL) {
			columnValueMap.put(ref, lastContents.toString());
			return;
		}
		// t元素也包含字符串
		if (isTElement) {
			// 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
			String value = lastContents.toString().trim();
			columnValueMap.put(ref, value);
			isTElement = false;
		} else if ("v".equals(name)) {
			// v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
			String value = this.getDataValue(lastContents.toString().trim(), "");
			columnValueMap.put(ref, value);
		} else {
			if (name.equals("row")) {
				// 默认第一行为表头，以该行单元格数目为最大数目
				if (curRow == 0) {
					maxRef = ref;
				}
				//如果 columnValueList 都是为空串，则不处理
				boolean isBlank = true;
				Iterator<String> valueIterator = columnValueMap.values().iterator();
				while (valueIterator.hasNext()) {
					if(StringUtils.isNotBlank(valueIterator.next())) {
						isBlank = false;
						break;
					}
				}
				if (!isBlank) {
					padWhitespace();
					resultList.add(new HashMap<>(columnValueMap));
				}
				columnValueMap.clear();
				curRow++;
				preRef = null;
				ref = null;
			}
		}
	}

	/**
	 * 填充空格
	 * @return
	 */
	private void padWhitespace() {
		int maxRefInt = convert10(maxRef.replaceAll("\\d+", ""));
		while(maxRefInt > 0) {
			String key = convert26(maxRefInt--) + (curRow + 1);
			if(!columnValueMap.containsKey(key)) {
				columnValueMap.putIfAbsent(key, "");
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// 得到单元格内容的值
		lastContents.append(new String(ch, start, length));
	}

	/**
	 * 十进制改成26进制
	 *
	 * @param n
	 * @return
	 */
	private static String convert26(int n) {
		StringBuilder sb = new StringBuilder();
		while (n > 0) {
			int m = n % 26;
			if (m == 0)
				m = 26;
			sb.append((char) (m + 64));
			n = (n - m) / 26;
		}
		return sb.toString();
	}

	/**
	 * 26进制转10进制
	 *
	 * @param s
	 * @return
	 */
	private static int convert10(String s) {
		if (Objects.isNull(s) || s.trim().length() == 0) {
			return 0;
		}
		int n = 0;
		char[] charArray = s.toCharArray();
		for (int i = charArray.length - 1, j = 1; i >= 0; i--, j *= 26) {
			char c = Character.toUpperCase(charArray[i]);
			if (c < 'A' || c > 'Z') {
				return 0;
			}
			n += ((int) c - 64) * j;
		}
		return n;
	}

	public static void main(String[] args) {
		try {
			List<Map<String, String>> mapList = ExcelSaxReader.getInstance().process("/Users/lizebin/Desktop/心康云-华虞门店导入表（模板）(1).xlsx");
			System.out.println(JSON.toJSONString(mapList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}