package com.pengesoft.wy.util.commonUtils;

import com.alibaba.fastjson.JSONObject;
import com.caven.core.framework.CavenRuntimeException;
import com.caven.core.framework.CavenUtils;
import com.pengesoft.services.common.ConstDefines;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 配置 工具类
 * Created by gyy on 2016/11/18.
 */
public class PropertiesUtils {

    /**
     * 获取配置文件Element
     * @param path 配置文件路径
     */
    public  Element getPropertyRootElement(String path){
        File file = new File(path);
        System.out.println(path);
        if (!file.exists()) {
            throw new CavenRuntimeException("未找到配置文件");
        }
        DocumentBuilder builder = null;
        Element element = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
            org.w3c.dom.Document dom = builder.parse(file);
            element = dom.getDocumentElement();
        } catch (SAXException |ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return element;
    }


    /**
     * 获取 备案要件配置 信息
     * @param RecordType 业务类型
     * @param proType    配置类型
     * @param proVersion 版本
     */
    public Element getElementByThreeAttrStr(Element element,String RecordType,String proType,String proVersion){
        Element e2 = getElementByAttrKeyValue(element,RecordType);
        Element e3 = getElementByAttrKeyValue(e2,proType);
        Element e4 = getElementByAttrKeyValue(e3,proVersion);
        return e4;
    }

    /**
     * 获取 元素下 指定属性 key的值为 传入参数的元素
     * @param element
     * @param attr
     * @return
     */
    public Element getElementByAttrKeyValue(Element element,String attr){
        if (element==null) {
            System.out.println("未找到该节点,请检查节点名称是否正确");
            return null;
        }
        //获取  业务类型 节点
        NodeList nList = element.getElementsByTagName("item");
        for(int i=0;i<nList.getLength();i++){
            Element e = (Element) nList.item(i);
            if (attr.equals(e.getAttribute("key"))) {
                return e;
            }
        }
        return null;
    }

    /**
     * 获取element的text值的String[]
     * @param element
     */
    public String[] getElementTextStringArray(Element element){
        if (element==null) {
            System.out.println("未找到节点,没有返回结果");
            return null;
        }
        NodeList nodeList = element.getElementsByTagName("item");
        String str[] = new String[nodeList.getLength()];
        for(int i=0;i<nodeList.getLength();i++ ){
            Node n = nodeList.item(i);
            //System.out.println(n.getTextContent());
            str[i] = n.getTextContent();
        }
        return str;
    }

    public List<String> getElementTextStringList(Element element){
        if (element==null) {
            System.out.println("未找到节点,没有返回结果");
            return null;
        }
        NodeList nodeList = element.getElementsByTagName("item");
        List<String> str = new ArrayList<>();
        for(int i=0;i<nodeList.getLength();i++ ){
            Node n = nodeList.item(i);
            //System.out.println(n.getTextContent());
            str.add(n.getTextContent());
        }
        return str;
    }
    public String[] getStringArrayBythreeParam(String path,String recordType,String contentType,String version){
        if (CavenUtils.isEmptyOrNull(version)) {
            version = getTheSysVersionNow(path);
        }
        Element e = getPropertyRootElement(path);
        Element ele = getElementByThreeAttrStr(e,recordType,contentType,version);
        return   getElementTextStringArray(ele);
    }

    public List<String> getStringListBythreeParam(String path,String recordType,String contentType,String version){
        if (CavenUtils.isEmptyOrNull(version)) {
             version = getTheSysVersionNow(path);
        }
        Element e = getPropertyRootElement(path);
        Element ele = getElementByThreeAttrStr(e,recordType,contentType,version);
        return   getElementTextStringList(ele);
    }

    /**
     *
     * 只传 业务/配置类型 和 要取的类容类型 版本会通过系统控制自己获取
     * @param path
     * @param recordType
     * @param contentType
     * @return
     */
    public String[] getStringArrayByTwoMainParam(String path,String recordType,String contentType){
        Element e = getPropertyRootElement(path);
        String version = getTheSysVersionNow(path);
        Element ele = getElementByThreeAttrStr(e,recordType,contentType,version);
        return   getElementTextStringArray(ele);
    }

    public List getStringListBythreeParam(String path,String recordType,String contentType){
        Element e = getPropertyRootElement(path);
        String version = getTheSysVersionNow(path);
        Element ele = getElementByThreeAttrStr(e,recordType,contentType,version);
        return   getElementTextStringList(ele);
    }

    public Element getElementByThreeParam(String path,String recordType,String contentType,String version){
        Element e = getPropertyRootElement(path);
        Element ele = null;
        if(!CavenUtils.isEmptyOrNull(version))
            ele = getElementByThreeAttrStr(e, recordType, contentType, version);
        else{
            version = getTheSysVersionNow(path);
            ele = getElementByThreeAttrStr(e,recordType,contentType,version);
        }
        return ele;
    }

    /**
     * 获取当前系统的版本控制
     * @param path
     * @return
     */
    public String getTheSysVersionNow(String path){
        Element e = getPropertyRootElement(path);
        Element ele = getElementByThreeAttrStr(e,"系统控制","版本控制","目前版本");
        return  getElementTextStringArray(ele)[0];
    }


    /**
     * 获取当前系统的版本控制
     * @param fileName 配置文件名称
     * @return
     */
    public String getTheSysVersionNowByFileName(String fileName){
        PropertiesUtils p = new PropertiesUtils();
        String readPath = getRealFilePath(fileName);
        Element e = p.getPropertyRootElement(readPath);
        Element ele = p.getElementByThreeAttrStr(e,"系统控制","版本控制","目前版本");
        return  p.getElementTextStringArray(ele)[0];
    }

    /**
     * 获取当前系统的版本
     * @return
     */
    public String getTheSysVersion(){
        return getTheSysVersionNowByFileName("businessPro.xml");
    }


    /**
     * 获取当前系统的审批模式
     * @return
     */
    public String getTheSysApproveModelNowByFileName(String fileNale){
        PropertiesUtils p = new PropertiesUtils();
        String readPath = getRealFilePath(fileNale);
        String versionNow = getTheSysVersionNow(readPath);
        Element e = p.getPropertyRootElement(readPath);
        Element ele = p.getElementByThreeAttrStr(e,"系统控制","审批控制",versionNow);
        Element endEle = p.getElementByAttrKeyValue(ele, "审批模式");
        return  p.getElementTextStringArray(endEle)[0];
    }

    /**
     * 获取当前系统的审批模式
     * @return
     */
    public String getTheSysApproveModelNow(){
        return getTheSysApproveModelNowByFileName("businessPro.xml");
    }


    /**
     * 获取当前系统的审批视图
     * @return
     */
    public String getTheSysApproveViewNowByFileName(String fileNale){
        PropertiesUtils p = new PropertiesUtils();
        String readPath = getRealFilePath(fileNale);
        String versionNow = getTheSysVersionNow(readPath);
        Element e = p.getPropertyRootElement(readPath);
        Element ele = p.getElementByThreeAttrStr(e,"系统控制","审批控制",versionNow);
        Element endEle = p.getElementByAttrKeyValue(ele, "审批视图");
        return  p.getElementTextStringArray(endEle)[0];
    }

    /**
     * 检查获取的元素String[] 里面 是否有要循环数据的 html
     */
    public List<String>  checkIsCycleHtml(String readPath,String recordType,String contentType,String version){

        Element ele = getElementByThreeParam(readPath, recordType, contentType, version);
        JSONObject[] jsonObjects= getValueAndAttrJsonbjStr(ele, "value");
        List<String> list =  new ArrayList<>();
        for(JSONObject e: jsonObjects){
            if ("true".equals(String.valueOf(e.get("cycle")))) {
                if(e.get("footIndex")==null)
                    throw new CavenRuntimeException("未配置循环打印界面下标！");
                list.add(String.valueOf(e.get("footIndex")));
            }
        }
        return list;
    }

    public boolean checkIsAlonPage(String readPath,String recordType,String contentType,String version,String footIndex) {
        Element ele = getElementByThreeParam(readPath, recordType, contentType, version);
        JSONObject[] jsonObjects= getValueAndAttrJsonbjStr(ele, "value");
        for(JSONObject e: jsonObjects){
            if (footIndex.equals(String.valueOf(e.get("footIndex")))&&
                    "true".equals(String.valueOf(e.get("isAlonePage")))) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIsAlonPage(Element ele,String footIndex) {
        JSONObject[] jsonObjects= getValueAndAttrJsonbjStr(ele, "value");
        for(JSONObject e: jsonObjects){
            if (footIndex.equals(String.valueOf(e.get("footIndex")))&&
                    "true".equals(String.valueOf(e.get("isAlonePage")))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查获取的元素String[] 里面 是否有要循环数据的 html
     */
    public List<String>  checkIsCycleHtml(Element ele){
       /* Element ele=null;
        if (CavenUtils.isEmptyOrNull(version)) {
            String Vser = getTheSysVersionNow(readPath);
            ele = getElementByThreeParam(readPath, proType, contentType, Vser);
        } else {
            ele = getElementByThreeParam(readPath, proType, contentType, version);
        }*/
        JSONObject[] jsonObjects= getValueAndAttrJsonbjStr(ele, "value");
        List<String> list = null;
        for(JSONObject e: jsonObjects){
            if ("true".equals(String.valueOf(e.get("cycle")))) {
                list = new ArrayList<>();
                list.add(String.valueOf(e.get("footIndex")));
            }
        }
        return list;
    }

    /**
     * 检查是否为 需要判定才赋值的html
     * @return 返回判定下标 matchIndex
     */
    public List<String> checkIsMatchHtml(Element ele){
        JSONObject[] jsonObjects= getValueAndAttrJsonbjStr(ele, "value");
        List<String> list = null;
        for(JSONObject e: jsonObjects){
            if (e.get("matchIndex")!=null) {
                list = new ArrayList<>();
                list.add(String.valueOf(e.get("matchIndex")));
            }
        }
        return list;
    }

    /**
     * 获取节点下
     *  所有子元素的内容 和每个元素的属性值 以JSONObject格式存入JSONObject[]返回
     * @param element
     * @param keyName 将每个元素的内容Content 存放到JsonObject的key键 名称 比如传入为title  那么键值对为：{title: 内容···}
     * @return
     */
    public JSONObject[] getValueAndAttrJsonbjStr(Element element,String keyName) {
        if (element==null) {
            System.out.println("未找到节点,没有返回结果");
            return null;
        }
        if (CavenUtils.isEmptyOrNull(keyName)) {
            keyName = "value";
        }
        NodeList nodeList = element.getElementsByTagName("item");
        JSONObject str[] = new JSONObject[nodeList.getLength()];
        for(int i=0;i<nodeList.getLength();i++ ){
            JSONObject addJson = new JSONObject();
            Node n = nodeList.item(i);
            NamedNodeMap nnm = n.getAttributes();
            for(int j=0;j<nnm.getLength();j++){
                Node nod = nnm.item(j);
                addJson.put(nod.getNodeName(), nod.getNodeValue());
            }
            //addJson.put("title",n.getTextContent());
            addJson.put(keyName,n.getTextContent());
            str[i] = addJson;
        }
        return str;
    }

    /**
     * 获取conf路径下 文件的路径
     * @param fileName
     * @return
     */
    public String getRealFilePath(String fileName) {
        String paht1 =this.getClass().getClassLoader().getResource("") + "";
        String readPath = paht1.substring("file:/".length(), paht1.length()) + "conf/" + fileName;
        return readPath;
    }

    /**
     * 取出 元素的 属性值
     * @param fileName  配置文件名
     * @param theAttrName 元素属性名称
     * @param reTypeKey  根元素下 各子节点的key
     * @param conTypeKey 子节点内 配置类型 的key 可为空
     * @param versionKey  版本的key  可为空
     */
    public String getAttrValueBykey(String fileName,String theAttrName,String reTypeKey,String conTypeKey,String versionKey){
        String readPath= getRealFilePath(fileName);
        Element e = getPropertyRootElement(readPath);
        Element ele = getElementByAttrKeyValue(e, reTypeKey);
        if (!CavenUtils.isEmptyOrNull(conTypeKey)) {
            ele=getElementByAttrKeyValue(ele, conTypeKey);
            if (!CavenUtils.isEmptyOrNull(versionKey)) {
                ele=getElementByAttrKeyValue(ele, versionKey);
            }
        }
        return  ele.getAttribute(theAttrName);
    }

    /**
     * 获得版本元素下 footindex为footIndexValue的子元素的attrName属性的值
     * @param ele 传入的父元素
     * @param footIndexValue
     * @return
     */
    public String getCycleDataByFootIndexAndDataAttr(Element ele,String footIndexValue,String attrName){
        NodeList nList = ele.getElementsByTagName("item");
        for(int i=0;i<nList.getLength();i++){
            Element e = (Element) nList.item(i);
            if (footIndexValue.equals(e.getAttribute("footIndex"))) {
                return e.getAttribute(attrName);
            }
        }
        return "";
    }
    /**
     * 取得 利用matchIndex找到的属性值
     * @param ele 传入的父元素
     * @param matchIndexValue
     * @return
     */
    public String getMatchDataByMatchIndexAndDataAttr(Element ele,String matchIndexValue,String attrName) {
        NodeList nList = ele.getElementsByTagName("item");
        for(int i=0;i<nList.getLength();i++){
            Element e = (Element) nList.item(i);
            if (matchIndexValue.equals(e.getAttribute("matchIndex"))) {
                return e.getAttribute(attrName);
            }
        }
        return "";
    }



    /**
     *  获取指定properties 文件里面的配置值
     * @param proFileName 指定的配置文件名称
     * @param properKeyName 要获取值的 配置文件里配置的 key
     * @return
     */
    public static String getSpringProperValue(String proFileName,String properKeyName){
        //String file4saveDownURL = new PropertiesUtils().getRealFilePath("spring.properties");
        String file4saveDownURL = new PropertiesUtils().getRealFilePath(proFileName);
        File fileDir = new File(file4saveDownURL);
        Properties pro = new Properties();
        if (!fileDir.exists()) {
            throw new CavenRuntimeException("未找到spring.properties文件!");
            //fileDir.mkdir();
        }
        try {
            pro.load(new FileInputStream(fileDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = pro.getProperty(properKeyName);
        pro.clear();
        return str;
    }

    /**
     * 获取WEB_INF的父文件路径   可用于找文件夹或者生成文件
     * @return
     */
    public  String getWEB_INFRootPath(){
        String thisPath = this.getClass().getClassLoader().getResource("")+"";
        return   thisPath.substring("file:/".length(),thisPath.indexOf("WEB-INF"));
    }

    /**
     * 获取下载附件文件夹路径
     * @return
     */
    public  static String getDownFileDirName(){
        return  PropertiesUtils.getSpringProperValue("spring.properties","downFileDirName");
    }

    //测试
    static public void main(String[] args){
         PropertiesUtils p = new PropertiesUtils();
        Element e = p.getPropertyRootElement("out/artifacts/gzwy_webapp_war_exploded/WEB-INF/classes/conf/businessPro.xml");
        //Element ele = p.getElementByThreeAttrStr(e,"系统控制","审批控制","审批模式");
        Element ele = p.getElementByThreeAttrStr(e,String.valueOf(ConstDefines.EBusinessType.物业服务合同备案.getValue()),"备案回执","广州版本");
        //String[] dd = p.getStringArrayByTwoMainParam("out/artifacts/gzwy_webapp_war_exploded/WEB-INF/classes/conf/businessPro.xml","通用","备案补正");
        ////JSONObject[] dd = p.getValueAndAttrJsonbjStr(ele,"value");
        //for(String se: dd){
        //    System.out.println(se);
        //}
        System.out.println(p.getWEB_INFRootPath());
        p.getElementTextStringArray(ele);
    }


}