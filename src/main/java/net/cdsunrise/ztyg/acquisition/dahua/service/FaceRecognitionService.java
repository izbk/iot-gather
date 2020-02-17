package net.cdsunrise.ztyg.acquisition.dahua.service;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.lib.NetSDKLib;
import main.java.com.netsdk.lib.ToolKits;
import net.cdsunrise.ztyg.acquisition.dahua.module.FaceRecognitionModule;
import net.cdsunrise.ztyg.acquisition.dahua.module.LoginModule;
import net.cdsunrise.ztyg.acquisition.dahua.module.RealPlayModule;
import net.cdsunrise.ztyg.acquisition.dahua.vo.*;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 人脸识别服务
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class FaceRecognitionService {
    private static boolean isAttach = false;
    // 订阅句柄
    public static NetSDKLib.LLong m_hAttachHandle = new NetSDKLib.LLong(0);
    // 预览句柄
    public static NetSDKLib.LLong m_hPlayHandle = new NetSDKLib.LLong(0);

    /**
     * 登出
     */
    public void logout() {
        FaceRecognitionModule.renderPrivateData(m_hPlayHandle, 0);
        RealPlayModule.stopRealPlay(m_hPlayHandle);
        FaceRecognitionModule.stopRealLoadPicture(m_hAttachHandle);
        LoginModule.logout();
    }

    /**
     * 订阅通道
     * @param channel
     */
    public void realLoadPicture(int channel) {
        if(!isAttach) {
            // 订阅通道
            m_hAttachHandle = FaceRecognitionModule.realLoadPicture(channel, AnalyzerDataCB.getInstance());
            if(m_hAttachHandle.longValue() != 0) {
                isAttach = true;
            } else {
                log.error("订阅失败,code:{},message:{}", ToolKits.getErrorCodeShow(), Res.string().getErrorMessage());
            }
        } else {
            FaceRecognitionModule.stopRealLoadPicture(m_hAttachHandle);
            isAttach = false;
        }
    }

    /**
     *  查找所有人脸库
     */
    public List<GroupInfoVo> findGroupInfo() {
        // 查询人脸库
        NetSDKLib.NET_FACERECONGNITION_GROUP_INFO[] groupInfoArr = FaceRecognitionModule.findGroupInfo("");
        List<GroupInfoVo> groupInfoVoList = new ArrayList<>();
        if(groupInfoArr != null) {
            for(int i = 0; i < groupInfoArr.length; i++) {
                GroupInfoVo vo = new GroupInfoVo();
                vo.setGroupId(new String(groupInfoArr[i].szGroupId).trim());
                try {
                    vo.setGroupName(new String(groupInfoArr[i].szGroupName, "GBK").trim());
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                vo.setGroupSize(String.valueOf(groupInfoArr[i].nGroupSize).trim());
                groupInfoVoList.add(vo);
            }
        }
        return groupInfoVoList;
    }

    /**
     * 增加人脸库
     * @param groupName
     * @return
     */
    public boolean addGroupInfo(String groupName){
        return FaceRecognitionModule.addGroup(groupName);
    }

    /**
     * 修改人脸库
     * @param groupName
     * @return
     */
    public boolean modifyGroupInfo(String groupName,String groupId){
        return FaceRecognitionModule.modifyGroup(groupName,groupId);
    }
    /**
     * 删除人脸库
     * @param groupName
     * @return
     */
    public boolean deleteGroupInfo(String groupId){
        return FaceRecognitionModule.deleteGroup(groupId);
    }

    /**
     * 删除人员信息
     * @param groupName
     * @return
     */
    public boolean deletePersonInfo(String groupId,String uid){
        return FaceRecognitionModule.delPerson(groupId,uid);
    }

    /**
     * 添加人员
     * @param groupId  人脸库ID
     * @param memory  图片路径
     * @param personName 姓名
     * @param sex  性别
     * @param isBirthday 是否下发生日
     * @param birthday  生日
     * @param byIdType  证件类型
     * @param idNo  证件号
     * @return
     */
    public boolean addPersonInfo(String groupId,
                                 String picPath,
                                 String personName,
                                 int sex,
                                 boolean isBirthday,
                                 String birthday,
                                 int byIdType,
                                 String idNo){
        return FaceRecognitionModule.addPerson(groupId, ToolKits.readPictureFile(picPath), personName, sex, isBirthday, birthday, byIdType, idNo);
    }

    /**
     * 修改人员信息
     * @param groupId 人脸库ID
     * @param uid  人员唯一标识符
     * @param picPath  图片路径
     * @param personName  姓名
     * @param sex  性别
     * @param isBirthday  是否下发生日
     * @param birthday  生日
     * @param byIdType  证件类型
     * @param idNo  证件号
     * @return true：成功 ,  false：失败
     */
    public static boolean modifyPerson(String groupId,
                                       String uid,
                                       String picPath,
                                       String personName,
                                       int sex,
                                       boolean isBirthday,
                                       String birthday,
                                       int byIdType,
                                       String idNo){
        return FaceRecognitionModule.modifyPerson(groupId, uid, ToolKits.readPictureFile(picPath), personName, sex, isBirthday, birthday, byIdType, idNo);
    }

    /**
     * 查找人员
     * @param groupName
     * @return
     */
    public List<PersonInfoVo> findPersonInfo(String groupId,boolean isStartBirthday,String startTime,boolean isEndBirthday,String endTime,
                                                 String name,int sex,int idType,String idNo,int page,int size){
        int count = FaceRecognitionModule.startFindPerson(groupId,
                isStartBirthday, startTime, isEndBirthday, endTime, name, sex, idType, idNo);
        List<PersonInfoVo> personInfoList = new ArrayList();
        if(count > 0){
            NetSDKLib.CANDIDATE_INFOEX[] stuCandidatesEx = FaceRecognitionModule.doFindPerson((page-1)*size, 17);
            personInfoList =  parsePersonInfo(stuCandidatesEx);
        }
        return personInfoList;
    }

    /**
     * 以人脸库的角度进行布控
     * @param groupId 人脸库ID
     * @param hashMap  key：撤控通道     value：相似度
     */
    public boolean putDisposition(String groupId, HashMap<Integer, Integer> hashMap){
        return FaceRecognitionModule.putDisposition(groupId, hashMap);
    }

    /**
     * 以人脸库的角度进行撤控
     * @param groupId 人脸库ID
     * @param arrayList 撤控通道列表
     */
    public static boolean delDisposition(String groupId, ArrayList<Integer> arrayList){
        return FaceRecognitionModule.delDisposition(groupId, arrayList);
    }

    /**
     * 获取查找句柄
     * @param nChn  通道号
     * @param startTime 开始时间
     * @param endTime  结束时间
     */
    public List<String> findFile(int nChn, String startTime, String endTime){
        int count = 0;  	  // 循环查询了几次
        int index = 0;	 	  // index + 1 为查询到的总个数
        int nFindCount = 10;  // 每次查询的个数
        StringBuffer message = null;
        List<String> fileList = new ArrayList<>();

        // 获取查询句柄
        if(!FaceRecognitionModule.findFile(nChn, startTime, endTime)) {
            return fileList;
        }

        // 查询具体信息, 循环查询， nFindCount为每次查询的个数
        while(true) {
            NetSDKLib.MEDIAFILE_FACERECOGNITION_INFO[] msg = FaceRecognitionModule.findNextFile(nFindCount);
            if(msg == null) {
                break;
            }
            for(int i = 0; i < msg.length; i++) {
                index = i + count * nFindCount + 1;
                // 清空
                message = new StringBuffer();
                message.append("[" + index + "]通道号 :" + msg[i].nChannelId + "\n");
                message.append("[" + index + "]报警发生时间 :" + msg[i].stTime.toStringTime() + "\n");
                message.append("[" + index + "]全景图 :" + new String(msg[i].stGlobalScenePic.szFilePath).trim() + "\n");
                message.append("[" + index + "]人脸图路径 :" + new String(msg[i].stObjectPic.szFilePath).trim() + "\n");
                message.append("[" + index + "]匹配到的候选对象数量 :" + msg[i].nCandidateNum + "\n");
                for(int j = 0; j < msg[i].nCandidateNum; j++) {
                    for(int k = 0; k < msg[i].stuCandidatesPic[j].nFileCount; k++) {
                        message.append("[" + index + "]对比图路径 :" + new String(msg[i].stuCandidatesPic[j].stFiles[k].szFilePath).trim() + "\n");
                    }
                }

                message.append("[" + index + "]匹配到的候选对象数量 :" + msg[i].nCandidateExNum + "\n");
                // 对比信息
                for(int j = 0; j < msg[i].nCandidateExNum; j++) {
                    message.append("[" + index + "]人员唯一标识符 :" + new String(msg[i].stuCandidatesEx[j].stPersonInfo.szUID).trim() + "\n");

                    // 以下参数，设备有些功能没有解析，如果想要知道   对比图的人员信息，可以根据上面获取的 szUID，来查询人员信息。
                    // findFaceRecognitionDB() 此示例的方法是根据 GroupId来查询的，这里的查询，GroupId不填，根据 szUID 来查询
                    message.append("[" + index + "]姓名 :" + new String(msg[i].stuCandidatesEx[j].stPersonInfo.szPersonName).trim() + "\n");
                    message.append("[" + index + "]相似度 :" + msg[i].stuCandidatesEx[j].bySimilarity + "\n");
                    message.append("[" + index + "]年龄 :" + msg[i].stuCandidatesEx[j].stPersonInfo.byAge + "\n");
                    message.append("[" + index + "]人脸库名称 :" + new String(msg[i].stuCandidatesEx[j].stPersonInfo.szGroupName).trim() + "\n");
                    message.append("[" + index + "]人脸库ID :" + new String(msg[i].stuCandidatesEx[j].stPersonInfo.szGroupID).trim() + "\n");
                }
                message.append("\n");
                fileList.add(message.toString());
            }
            if (msg.length < nFindCount) {
                break;
            } else {
                count ++;
            }
        }
        // 关闭查询接口
        FaceRecognitionModule.findCloseFile();
        return fileList;
    }

    /**
     * 下载图片, 用于修改人员信息
     * @param szFileName 需要下载的文件名
     * @param pszFileDst 存放文件路径
     */
    public static boolean downloadPersonPic(String szFileName, String pszFileDst){
        return FaceRecognitionModule.downloadPersonPic(szFileName, pszFileDst);
    }

    /**
     * 解析数据为列表
     * @param stuCandidatesEx
     * @return
     */
    private List<PersonInfoVo> parsePersonInfo(NetSDKLib.CANDIDATE_INFOEX[] stuCandidatesEx) {
        List<PersonInfoVo> personInfoList = new ArrayList();
        if(stuCandidatesEx != null) {
            for(int i = 0; i < stuCandidatesEx.length; i++) {
                PersonInfoVo personInfo = new PersonInfoVo();
                // UID
                personInfo.setUid(new String(stuCandidatesEx[i].stPersonInfo.szUID).trim());
                // 姓名
                try {
                    personInfo.setName(new String(stuCandidatesEx[i].stPersonInfo.szPersonName, "GBK").trim());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // 性别
                personInfo.setSex(Res.string().getSex(stuCandidatesEx[i].stPersonInfo.bySex & 0xff));
                // 生日
                personInfo.setBirthday(String.valueOf((int)stuCandidatesEx[i].stPersonInfo.wYear) + "-" +
                        String.valueOf( stuCandidatesEx[i].stPersonInfo.byMonth & 0xff) + "-" +
                        String.valueOf(stuCandidatesEx[i].stPersonInfo.byDay & 0xff));
                // 证件类型
                personInfo.setIdType(Res.string().getIdType(stuCandidatesEx[i].stPersonInfo.byIDType & 0xff));
                // 证件号
                try {
                    personInfo.setIdNo(new String(stuCandidatesEx[i].stPersonInfo.szID, "GBK").trim());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return personInfoList;
    }

    /**
     * 解析人脸识别信息
     * @param facerecognitionInfo
     * @param index
     * @return
     */
    private FaceRecognitionInfoVo parseFaceRecognitionEventInfo(
                                                     NetSDKLib.DEV_EVENT_FACERECOGNITION_INFO facerecognitionInfo,
                                                     int index) {
        FaceRecognitionInfoVo faceRecognitionInfoVo = new FaceRecognitionInfoVo();
        FaceVo faceVo = new FaceVo();
        CandidateVo candidateVo = new CandidateVo();
        // 时间
        if(facerecognitionInfo.UTC != null
                && !facerecognitionInfo.UTC.toString().equals("")) {
            faceRecognitionInfoVo.setTime(facerecognitionInfo.UTC.toString());
        }

        // 人脸信息
        if(facerecognitionInfo.stuFaceData != null) {
            faceVo.setSex(Res.string().getSex(facerecognitionInfo.stuFaceData.emSex));
            if(facerecognitionInfo.stuFaceData.nAge == -1) {
                faceVo.setAge(Res.string().getUnKnow());
            } else {
                faceVo.setAge(String.valueOf(facerecognitionInfo.stuFaceData.nAge));
            }
            faceVo.setRace(Res.string().getRace(facerecognitionInfo.stuFaceData.emRace));
            faceVo.setEye(Res.string().getEyeState(facerecognitionInfo.stuFaceData.emEye));
            faceVo.setMouth(Res.string().getMouthState(facerecognitionInfo.stuFaceData.emMouth));
            faceVo.setMask(Res.string().getMaskState(facerecognitionInfo.stuFaceData.emMask));
            faceVo.setBeard(Res.string().getBeardState(facerecognitionInfo.stuFaceData.emBeard));
        }

        // 候选人信息
        if(facerecognitionInfo.nRetCandidatesExNum != 0
                && index != -1) {
            candidateVo.setSex(Res.string().getSex(facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.bySex & 0xff));
            candidateVo.setBirthday(String.valueOf((int)facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.wYear) + "-"
                    + String.valueOf(facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.byMonth & 0xff) + "-"
                    + String.valueOf(facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.byDay & 0xff));

            try {
                candidateVo.setName(new String(facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.szPersonName, "GBK").trim());
                candidateVo.setIdNo(new String(facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.szID, "GBK").trim());
                candidateVo.setGroupId(new String(facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.szGroupID, "GBK").trim());
                candidateVo.setGroupName(new String(facerecognitionInfo.stuCandidatesEx[index].stPersonInfo.szGroupName, "GBK").trim());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            candidateVo.setSimilarity(String.valueOf(facerecognitionInfo.stuCandidatesEx[index].bySimilarity & 0xff));
        }
        faceRecognitionInfoVo.setFaceVo(faceVo);
        faceRecognitionInfoVo.setCandidateVo(candidateVo);
        return faceRecognitionInfoVo;
    }

    /**
     *  解析人脸检测的数据
     * @param facedetectInfo
     * @return
     */
    private FaceRecognitionInfoVo showFaceDetectEventInfo(NetSDKLib.DEV_EVENT_FACEDETECT_INFO facedetectInfo) {

        FaceRecognitionInfoVo faceRecognitionInfoVo = new FaceRecognitionInfoVo();
        FaceVo faceVo = new FaceVo();
        CandidateVo candidateVo = new CandidateVo();
        // 时间
        if(facedetectInfo.UTC != null
                && !facedetectInfo.UTC.toString().equals("")) {
            faceRecognitionInfoVo.setTime(facedetectInfo.UTC.toString());
        }

        // 人脸信息
        faceVo.setSex(Res.string().getSex(facedetectInfo.emSex));
        if(facedetectInfo.nAge == -1) {
            faceVo.setAge(Res.string().getUnKnow());
        } else {
            faceVo.setAge(String.valueOf(facedetectInfo.nAge));
        }
        faceVo.setRace(Res.string().getRace(facedetectInfo.emRace));
        faceVo.setEye(Res.string().getEyeState(facedetectInfo.emEye));
        faceVo.setMouth(Res.string().getMouthState(facedetectInfo.emMouth));
        faceVo.setMask(Res.string().getMaskState(facedetectInfo.emMask));
        faceVo.setBeard(Res.string().getBeardState(facedetectInfo.emBeard));
        faceRecognitionInfoVo.setFaceVo(faceVo);
        faceRecognitionInfoVo.setCandidateVo(candidateVo);
        return faceRecognitionInfoVo;
    }

    /**
     * 智能分析数据回调
     */
    private static class AnalyzerDataCB implements NetSDKLib.fAnalyzerDataCallBack {
        private AnalyzerDataCB() {}

        private static class AnalyzerDataCBHolder {
            private static final AnalyzerDataCB instance = new AnalyzerDataCB();
        }

        public static AnalyzerDataCB getInstance() {
            return AnalyzerDataCB.AnalyzerDataCBHolder.instance;
        }

        @Override
        public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
                          Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
                          Pointer dwUser, int nSequence, Pointer reserved) {
            if (lAnalyzerHandle.longValue() == 0 || pAlarmInfo == null) {
                return -1;
            }
            switch(dwAlarmType) {
                // 人脸识别事件
                case NetSDKLib.EVENT_IVS_FACERECOGNITION: {
                    // DEV_EVENT_FACERECOGNITION_INFO 结构体比较大，new对象会比较耗时， ToolKits.GetPointerData内容拷贝是不耗时的。
                    // 如果多台设备或者事件处理比较频繁，可以考虑将 static DEV_EVENT_FACERECOGNITION_INFO msg = new DEV_EVENT_FACERECOGNITION_INFO(); 改为全局。
                    // 写成全局，是因为每次new花费时间较多, 如果改为全局，此case下的处理需要加锁
                    // 加锁，是因为共用一个对象，防止数据出错

                    // 耗时800ms左右
                    NetSDKLib.DEV_EVENT_FACERECOGNITION_INFO msg = new NetSDKLib.DEV_EVENT_FACERECOGNITION_INFO();
                    // 耗时20ms左右
                    ToolKits.GetPointerData(pAlarmInfo, msg);
                    // 保存图片，获取图片缓存
                    // 耗时20ms左右
                    saveFaceRecognitionPic(pBuffer, dwBufSize, msg);
                    // 列表、图片界面显示
                    // TODO

                    // 释放内存
                    msg = null;
                    System.gc();
                    break;
                }
                // 人脸检测
                case NetSDKLib.EVENT_IVS_FACEDETECT: {
                    NetSDKLib.DEV_EVENT_FACEDETECT_INFO msg = new NetSDKLib.DEV_EVENT_FACEDETECT_INFO();
                    ToolKits.GetPointerData(pAlarmInfo, msg);
                    // 保存图片，获取图片缓存
                    saveFaceDetectPic(pBuffer, dwBufSize, msg);

                    // 列表、图片界面显示
                    // TODO

                    // 释放内存
                    msg = null;
                    System.gc();
                    break;
                }
                default:
                    break;
            }
            return 0;
        }

        /**
         * 保存人脸识别事件图片
         * @param pBuffer 抓拍图片信息
         * @param dwBufSize 抓拍图片大小
         * @param faceRecognitionInfo 人脸识别事件信息
         * @return int 候选图案索引index
         */
        public int saveFaceRecognitionPic(Pointer pBuffer, int dwBufSize,
                                           NetSDKLib.DEV_EVENT_FACERECOGNITION_INFO faceRecognitionInfo) {
            int index = -1;
            BufferedImage globalBufferedImage = null;
            BufferedImage personBufferedImage = null;
            BufferedImage candidateBufferedImage = null;

            File path = new File("./FaceRecognition/");
            if (!path.exists()) {
                path.mkdir();
            }
            if (pBuffer == null || dwBufSize <= 0) {
                return index;
            }

            /////////////// 保存全景图 ///////////////////
            if(faceRecognitionInfo.bGlobalScenePic == 1) {

                String strGlobalPicPathName = path + "\\" + faceRecognitionInfo.UTC.toStringTitle() + "_FaceRecognition_Global.jpg";
                byte[] bufferGlobal = pBuffer.getByteArray(faceRecognitionInfo.stuGlobalScenePicInfo.dwOffSet,
                        faceRecognitionInfo.stuGlobalScenePicInfo.dwFileLenth);
                ByteArrayInputStream byteArrInputGlobal = new ByteArrayInputStream(bufferGlobal);
                try {
                    globalBufferedImage = ImageIO.read(byteArrInputGlobal);
                    if(globalBufferedImage != null) {
                        File globalFile = new File(strGlobalPicPathName);
                        if(globalFile != null) {
                            ImageIO.write(globalBufferedImage, "jpg", globalFile);
                        }
                    }
                } catch (IOException e) {
                    log.error("保存全景图失败:{}", e.getMessage());
                }
            }

            /////////////// 保存人脸图 /////////////////////////
            if(faceRecognitionInfo.stuObject.stPicInfo != null) {
                String strPersonPicPathName = path + "\\" + faceRecognitionInfo.UTC.toStringTitle() + "_FaceRecognition_Person.jpg";
                byte[] bufferPerson = pBuffer.getByteArray(faceRecognitionInfo.stuObject.stPicInfo.dwOffSet,
                        faceRecognitionInfo.stuObject.stPicInfo.dwFileLenth);
                ByteArrayInputStream byteArrInputPerson = new ByteArrayInputStream(bufferPerson);
                try {
                    personBufferedImage = ImageIO.read(byteArrInputPerson);
                    if(personBufferedImage != null) {
                        File personFile = new File(strPersonPicPathName);
                        if(personFile != null) {
                            ImageIO.write(personBufferedImage, "jpg", personFile);
                        }
                    }
                } catch (IOException e) {
                    log.error("保存人脸图失败:{}", e.getMessage());
                }
            }

            ///////////// 保存对比图 //////////////////////
            if(faceRecognitionInfo.nRetCandidatesExNum > 0
                    && faceRecognitionInfo.stuCandidatesEx != null) {
                int maxValue = -1;

                // 设备可能返回多张图片，这里只显示相似度最高的
                int[] nSimilary = new int[faceRecognitionInfo.nRetCandidatesExNum];
                for(int i = 0; i < faceRecognitionInfo.nRetCandidatesExNum; i++) {
                    nSimilary[i] = faceRecognitionInfo.stuCandidatesEx[i].bySimilarity & 0xff;
                }
                for(int i = 0; i < nSimilary.length; i++) {
                    if(maxValue < nSimilary[i]) {
                        maxValue = nSimilary[i];
                        index = i;
                    }
                }
                String strCandidatePicPathName = path + "\\" + faceRecognitionInfo.UTC.toStringTitle() + "_FaceRecognition_Candidate.jpg";
                // 每个候选人的图片个数：faceRecognitionInfo.stuCandidatesEx[index].stPersonInfo.wFacePicNum，
                // 正常情况下只有1张。如果有多张，此demo只显示第一张
                byte[] bufferCandidate = pBuffer.getByteArray(faceRecognitionInfo.stuCandidatesEx[index].stPersonInfo.szFacePicInfo[0].dwOffSet,
                        faceRecognitionInfo.stuCandidatesEx[index].stPersonInfo.szFacePicInfo[0].dwFileLenth);
                ByteArrayInputStream byteArrInputCandidate = new ByteArrayInputStream(bufferCandidate);
                try {
                    candidateBufferedImage = ImageIO.read(byteArrInputCandidate);
                    if(candidateBufferedImage != null) {
                        File candidateFile = new File(strCandidatePicPathName);
                        if(candidateFile != null) {
                            ImageIO.write(candidateBufferedImage, "jpg", candidateFile);
                        }
                    }
                } catch (IOException e) {
                    log.error("保存对比图失败:{}", e.getMessage());
                }
            }
            return index;
        }

        /**
         * 保存人脸检测事件图片
         * @param pBuffer 抓拍图片信息
         * @param dwBufSize 抓拍图片大小
         * @param faceDetectInfo 人脸检测事件信息
         */
        @SuppressWarnings("Duplicates")
        public void saveFaceDetectPic(Pointer pBuffer, int dwBufSize,
                                      NetSDKLib.DEV_EVENT_FACEDETECT_INFO faceDetectInfo) {
            File path = new File("./FaceDetection/");
            if (!path.exists()) {
                path.mkdir();
            }
            if (pBuffer == null || dwBufSize <= 0) {
                return;
            }
            int groupId = 0;
            // 小图的 stuObject.nRelativeID 来匹配大图的 stuObject.nObjectID，来判断是不是 一起的图片
            if(groupId != faceDetectInfo.stuObject.nRelativeID) {
                // 保存全景图
                BufferedImage personBufferedImage = null;
                groupId = faceDetectInfo.stuObject.nObjectID;

                String strGlobalPicPathName = path + "\\" + faceDetectInfo.UTC.toStringTitle() + "_FaceDetection_Global.jpg";
                byte[] bufferGlobal = pBuffer.getByteArray(0, dwBufSize);
                ByteArrayInputStream byteArrInputGlobal = new ByteArrayInputStream(bufferGlobal);

                try {
                    BufferedImage globalBufferedImage = ImageIO.read(byteArrInputGlobal);
                    if(globalBufferedImage != null) {
                        File globalFile = new File(strGlobalPicPathName);
                        if(globalFile != null) {
                            ImageIO.write(globalBufferedImage, "jpg", globalFile);
                        }
                    }
                } catch (IOException e2) {
                    log.error("保存全景图出错:{}",e2.getMessage());
                }
            } else if(groupId == faceDetectInfo.stuObject.nRelativeID){
                // 保存人脸图
                if(faceDetectInfo.stuObject.stPicInfo != null) {
                    String strPersonPicPathName = path + "\\" + faceDetectInfo.UTC.toStringTitle() + "_FaceDetection_Person.jpg";
                    byte[] bufferPerson = pBuffer.getByteArray(0, dwBufSize);
                    ByteArrayInputStream byteArrInputPerson = new ByteArrayInputStream(bufferPerson);
                    try {
                        BufferedImage personBufferedImage = ImageIO.read(byteArrInputPerson);
                        if(personBufferedImage != null) {
                            File personFile = new File(strPersonPicPathName);
                            if(personFile != null) {
                                ImageIO.write(personBufferedImage, "jpg", personFile);
                            }
                        }
                    } catch (IOException e2) {
                        log.error("保存人脸图出错:{}",e2.getMessage());
                    }
                }
            }
        }
    }
}