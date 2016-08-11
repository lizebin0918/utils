package com.yaodian.utils;

import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 表情工具类（接收微信的QQ表情符号和网页端的QQ表情符号进行转义）
 * @author lizebin
 *
 */
public class QQFaceConverterUtils {
	
	private static String QQ_FACE_MAPPING_JSON_STRING = "["
			+ "{\"data-title\":\"/微笑\",\"data-id\":\"100\",\"data-code\":\"/::)\"},"
			+ "{\"data-title\":\"/撇嘴\",\"data-id\":\"101\",\"data-code\":\"/::~\"},"
			+ "{\"data-title\":\"/色\",\"data-id\":\"102\",\"data-code\":\"/::B\"},"
			+ "{\"data-title\":\"/发呆\",\"data-id\":\"103\",\"data-code\":\"/::|\"},"
			+ "{\"data-title\":\"/得意\",\"data-id\":\"104\",\"data-code\":\"/:8-)\"},"
			+ "{\"data-title\":\"/流泪\",\"data-id\":\"105\",\"data-code\":\"/::<\"},"
			+ "{\"data-title\":\"/害羞\",\"data-id\":\"106\",\"data-code\":\"/::$\"},"
			+ "{\"data-title\":\"/闭嘴\",\"data-id\":\"107\",\"data-code\":\"/::X\"},"
			+ "{\"data-title\":\"/睡\",\"data-id\":\"108\",\"data-code\":\"/::Z\"},"
			+ "{\"data-title\":\"/大哭\",\"data-id\":\"109\",\"data-code\":\"/::'(\"},"
			+ "{\"data-title\":\"/尴尬\",\"data-id\":\"110\",\"data-code\":\"/::-|\"},"
			+ "{\"data-title\":\"/发怒\",\"data-id\":\"111\",\"data-code\":\"/::@\"},"
			+ "{\"data-title\":\"/调皮\",\"data-id\":\"112\",\"data-code\":\"/::P\"},"
			+ "{\"data-title\":\"/呲牙\",\"data-id\":\"113\",\"data-code\":\"/::D\"},"
			+ "{\"data-title\":\"/惊讶\",\"data-id\":\"114\",\"data-code\":\"/::O\"},"
			+ "{\"data-title\":\"/难过\",\"data-id\":\"115\",\"data-code\":\"/::(\"},"
			+ "{\"data-title\":\"/酷\",\"data-id\":\"116\",\"data-code\":\"/::+\"},"
			+ "{\"data-title\":\"/冷汗\",\"data-id\":\"117\",\"data-code\":\"/:--b\"},"
			+ "{\"data-title\":\"/抓狂\",\"data-id\":\"118\",\"data-code\":\"/::Q\"},"
			+ "{\"data-title\":\"/吐\",\"data-id\":\"119\",\"data-code\":\"/::T\"},"
			+ "{\"data-title\":\"/偷笑\",\"data-id\":\"120\",\"data-code\":\"/:,@P\"},"
			+ "{\"data-title\":\"/可爱\",\"data-id\":\"121\",\"data-code\":\"/:,@-D\"},"
			+ "{\"data-title\":\"/白眼\",\"data-id\":\"122\",\"data-code\":\"/::d\"},"
			+ "{\"data-title\":\"/傲慢\",\"data-id\":\"123\",\"data-code\":\"/:,@o\"},"
			+ "{\"data-title\":\"/饥饿\",\"data-id\":\"124\",\"data-code\":\"/::g\"},"
			+ "{\"data-title\":\"/困\",\"data-id\":\"125\",\"data-code\":\"/:|-)\"},"
			+ "{\"data-title\":\"/惊恐\",\"data-id\":\"126\",\"data-code\":\"/::!\"},"
			+ "{\"data-title\":\"/流汗\",\"data-id\":\"127\",\"data-code\":\"/::L\"},"
			+ "{\"data-title\":\"/憨笑\",\"data-id\":\"128\",\"data-code\":\"/::>\"},"
			+ "{\"data-title\":\"/大兵\",\"data-id\":\"129\",\"data-code\":\"/::,@\"},"
			+ "{\"data-title\":\"/奋斗\",\"data-id\":\"130\",\"data-code\":\"/:,@f\"},"
			+ "{\"data-title\":\"/咒骂\",\"data-id\":\"131\",\"data-code\":\"/::-S\"},"
			+ "{\"data-title\":\"/疑问\",\"data-id\":\"132\",\"data-code\":\"/:?\"},"
			+ "{\"data-title\":\"/嘘\",\"data-id\":\"133\",\"data-code\":\"/:,@x\"},"
			+ "{\"data-title\":\"/晕\",\"data-id\":\"134\",\"data-code\":\"/:,@@\"},"
			+ "{\"data-title\":\"/折磨\",\"data-id\":\"135\",\"data-code\":\"/::8\"},"
			+ "{\"data-title\":\"/衰\",\"data-id\":\"136\",\"data-code\":\"/:,@!\"},"
			+ "{\"data-title\":\"/骷髅\",\"data-id\":\"137\",\"data-code\":\"/:!!!\"},"
			+ "{\"data-title\":\"/敲打\",\"data-id\":\"138\",\"data-code\":\"/:xx\"},"
			+ "{\"data-title\":\"/再见\",\"data-id\":\"139\",\"data-code\":\"/:bye\"},"
			+ "{\"data-title\":\"/擦汗\",\"data-id\":\"140\",\"data-code\":\"/:wipe\"},"
			+ "{\"data-title\":\"/抠鼻\",\"data-id\":\"141\",\"data-code\":\"/:dig\"},"
			+ "{\"data-title\":\"/鼓掌\",\"data-id\":\"142\",\"data-code\":\"/:handclap\"},"
			+ "{\"data-title\":\"/糗大了\",\"data-id\":\"143\",\"data-code\":\"/:&-(\"},"
			+ "{\"data-title\":\"/坏笑\",\"data-id\":\"144\",\"data-code\":\"/:B-)\"},"
			+ "{\"data-title\":\"/左哼哼\",\"data-id\":\"145\",\"data-code\":\"/:<@\"},"
			+ "{\"data-title\":\"/右哼哼\",\"data-id\":\"146\",\"data-code\":\"/:@>\"},"
			+ "{\"data-title\":\"/哈欠\",\"data-id\":\"147\",\"data-code\":\"/::-O\"},"
			+ "{\"data-title\":\"/鄙视\",\"data-id\":\"148\",\"data-code\":\"/:>-|\"},"
			+ "{\"data-title\":\"/委屈\",\"data-id\":\"149\",\"data-code\":\"/:P-(\"},"
			+ "{\"data-title\":\"/快哭了\",\"data-id\":\"150\",\"data-code\":\"/::'|\"},"
			+ "{\"data-title\":\"/阴险\",\"data-id\":\"151\",\"data-code\":\"/:X-)\"},"
			+ "{\"data-title\":\"/亲亲\",\"data-id\":\"152\",\"data-code\":\"/::*\"},"
			+ "{\"data-title\":\"/吓\",\"data-id\":\"153\",\"data-code\":\"/:@x\"},"
			+ "{\"data-title\":\"/可怜\",\"data-id\":\"154\",\"data-code\":\"/:8*\"},"
			+ "{\"data-title\":\"/菜刀\",\"data-id\":\"155\",\"data-code\":\"/:pd\"},"
			+ "{\"data-title\":\"/西瓜\",\"data-id\":\"156\",\"data-code\":\"/:<W>\"},"
			+ "{\"data-title\":\"/啤酒\",\"data-id\":\"157\",\"data-code\":\"/:beer\"},"
			+ "{\"data-title\":\"/篮球\",\"data-id\":\"158\",\"data-code\":\"/:basketb\"},"
			+ "{\"data-title\":\"/乒乓\",\"data-id\":\"159\",\"data-code\":\"/:oo\"},"
			+ "{\"data-title\":\"/咖啡\",\"data-id\":\"160\",\"data-code\":\"/:coffee\"},"
			+ "{\"data-title\":\"/饭\",\"data-id\":\"161\",\"data-code\":\"/:eat\"},"
			+ "{\"data-title\":\"/猪头\",\"data-id\":\"162\",\"data-code\":\"/:pig\"},"
			+ "{\"data-title\":\"/玫瑰\",\"data-id\":\"163\",\"data-code\":\"/:rose\"},"
			+ "{\"data-title\":\"/凋谢\",\"data-id\":\"164\",\"data-code\":\"/:fade\"},"
			+ "{\"data-title\":\"/示爱\",\"data-id\":\"165\",\"data-code\":\"/:showlove\"},"
			+ "{\"data-title\":\"/爱心\",\"data-id\":\"166\",\"data-code\":\"/:heart\"},"
			+ "{\"data-title\":\"/心碎\",\"data-id\":\"167\",\"data-code\":\"/:break\"},"
			+ "{\"data-title\":\"/蛋糕\",\"data-id\":\"168\",\"data-code\":\"/:cake\"},"
			+ "{\"data-title\":\"/闪电\",\"data-id\":\"169\",\"data-code\":\"/:li\"},"
			+ "{\"data-title\":\"/炸弹\",\"data-id\":\"170\",\"data-code\":\"/:bome\"},"
			+ "{\"data-title\":\"/刀\",\"data-id\":\"171\",\"data-code\":\"/:kn\"},"
			+ "{\"data-title\":\"/足球\",\"data-id\":\"172\",\"data-code\":\"/:footb\"},"
			+ "{\"data-title\":\"/瓢虫\",\"data-id\":\"173\",\"data-code\":\"/:ladybug\"},"
			+ "{\"data-title\":\"/便便\",\"data-id\":\"174\",\"data-code\":\"/:shit\"},"
			+ "{\"data-title\":\"/月亮\",\"data-id\":\"175\",\"data-code\":\"/:moon\"},"
			+ "{\"data-title\":\"/太阳\",\"data-id\":\"176\",\"data-code\":\"/:sun\"},"
			+ "{\"data-title\":\"/礼物\",\"data-id\":\"177\",\"data-code\":\"/:gift\"},"
			+ "{\"data-title\":\"/拥抱\",\"data-id\":\"178\",\"data-code\":\"/:hug\"},"
			+ "{\"data-title\":\"/强\",\"data-id\":\"179\",\"data-code\":\"/:strong\"},"
			+ "{\"data-title\":\"/弱\",\"data-id\":\"180\",\"data-code\":\"/:weak\"},"
			+ "{\"data-title\":\"/握手\",\"data-id\":\"181\",\"data-code\":\"/:share\"},"
			+ "{\"data-title\":\"/胜利\",\"data-id\":\"182\",\"data-code\":\"/:v\"},"
			+ "{\"data-title\":\"/抱拳\",\"data-id\":\"183\",\"data-code\":\"/:@)\"},"
			+ "{\"data-title\":\"/勾引\",\"data-id\":\"184\",\"data-code\":\"/:jj\"},"
			+ "{\"data-title\":\"/拳头\",\"data-id\":\"185\",\"data-code\":\"/:@@\"},"
			+ "{\"data-title\":\"/差劲\",\"data-id\":\"186\",\"data-code\":\"/:bad\"},"
			+ "{\"data-title\":\"/爱你\",\"data-id\":\"187\",\"data-code\":\"/:lvu\"},"
			+ "{\"data-title\":\"/NO\",\"data-id\":\"188\",\"data-code\":\"/:no\"},"
			+ "{\"data-title\":\"/OK\",\"data-id\":\"189\",\"data-code\":\"/:ok\"},"
			+ "{\"data-title\":\"/爱情\",\"data-id\":\"190\",\"data-code\":\"/:love\"},"
			+ "{\"data-title\":\"/飞吻\",\"data-id\":\"191\",\"data-code\":\"/:<L>\"},"
			+ "{\"data-title\":\"/跳跳\",\"data-id\":\"192\",\"data-code\":\"/:jump\"},"
			+ "{\"data-title\":\"/发抖\",\"data-id\":\"193\",\"data-code\":\"/:shake\"},"
			+ "{\"data-title\":\"/怄火\",\"data-id\":\"194\",\"data-code\":\"/:<O>\"},"
			+ "{\"data-title\":\"/转圈\",\"data-id\":\"195\",\"data-code\":\"/:circle\"},"
			+ "{\"data-title\":\"/磕头\",\"data-id\":\"196\",\"data-code\":\"/:kotow\"},"
			+ "{\"data-title\":\"/回头\",\"data-id\":\"197\",\"data-code\":\"/:turn\"},"
			+ "{\"data-title\":\"/跳绳\",\"data-id\":\"198\",\"data-code\":\"/:skip\"},"
			+ "{\"data-title\":\"/挥手\",\"data-id\":\"199\",\"data-code\":\"/:oY\"},"
			+ "{\"data-title\":\"/激动\",\"data-id\":\"200\",\"data-code\":\"/:#-0\"},"
			+ "{\"data-title\":\"/街舞\",\"data-id\":\"201\",\"data-code\":\"/:hiphot\"},"
			+ "{\"data-title\":\"/献吻\",\"data-id\":\"202\",\"data-code\":\"/:kiss\"},"
			+ "{\"data-title\":\"/左太极\",\"data-id\":\"203\",\"data-code\":\"/:&<\"},"
			+ "{\"data-title\":\"/右太极\",\"data-id\":\"204\",\"data-code\":\"/:&>\"}]";
	
	private static JSONArray QQ_FACE_MAPPING_JSON_ARRAY = null;
	
	//微信QQ表情符号代码转换为真正的表情图片路径,图片路径已写死
    private static final String IMG_FILL_PATH = "<img style='width:24px;height:24px;' src='/assets/img/emotion/e{index}.gif'/>";
    
    static{
    		QQ_FACE_MAPPING_JSON_ARRAY = JSONArray.parseArray(QQ_FACE_MAPPING_JSON_STRING);
    }
    public static void main(String[] args) {
		System.out.println(QQ_FACE_MAPPING_JSON_ARRAY);
	}
    /**
     * 将 content 中 的 QQ表情符号代码转换为表情图片'<img>'标签
     * @param content
     * @return
     */
	public static String convertQQFace(String content) {
		if (null == content || "".equals(content.trim())) {
			return content;
		}
		for (int i = 0, size = QQ_FACE_MAPPING_JSON_ARRAY.size(); i < size; i++) {
			JSONObject _mapping = (JSONObject) QQ_FACE_MAPPING_JSON_ARRAY
					.get(i);
			String valueCode = _mapping.getString("data-code");
			String valueCHN = _mapping.getString("data-title");
			String index = _mapping.getString("data-id");
			if (content.contains(valueCode)) {
				// 对表情符号进行正则转义
				content = content.replaceAll(escapeExprSpecialWord(valueCode), IMG_FILL_PATH.replace("{index}", index));
				continue;
			}
			if (content.contains(valueCHN)) {
				content = content.replaceAll(valueCHN, IMG_FILL_PATH.replace("{index}", index));
				continue;
			}
		}
		return content;
	}
    
    /**
     * 对表情符号的正则表达式符号进行转义
     * @param keyword
     * @return
     */
    private static String escapeExprSpecialWord(String keyword) {  
        if (keyword != null && keyword.trim().length() > 0) {  
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };  
            for (String key : fbsArr) {  
                if (keyword.contains(key)) {  
                    keyword = keyword.replace(key, "\\" + key);  
                }  
            }  
        }  
        return keyword;  
    }
}
