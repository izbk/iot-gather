package net.cdsunrise.ztyg.acquisition.dahua.service;

import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.lib.NetSDKLib;
import main.java.com.netsdk.lib.ToolKits;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.dahua.module.GateModule;
import net.cdsunrise.ztyg.acquisition.dahua.vo.CardInfoVo;
import net.cdsunrise.ztyg.acquisition.dahua.vo.GateEventInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 闸机服务
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class GateService {

    private AnalyzerDataCB analyzerCallback = new AnalyzerDataCB();
    /**
     * 订阅实时上传智能分析数据
     * @param channelId
     * @param m_AnalyzerDataCB
     * @return
     */
    public NetSDKLib.LLong realLoadPic(int channelId, NetSDKLib.fAnalyzerDataCallBack m_AnalyzerDataCB){
        return GateModule.realLoadPic(channelId, m_AnalyzerDataCB);
    }

    /**
     * 停止上传智能分析数据－图片
     * @param attachHandle
     */
    public void stopRealLoadPic(NetSDKLib.LLong attachHandle){
        GateModule.stopRealLoadPic(attachHandle);
    }

    /**
     * 智能数据分析回调
     */
    private class AnalyzerDataCB implements NetSDKLib.fAnalyzerDataCallBack {
        private BufferedImage gateBufferedImage = null;
        @Override
        public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
                          Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
                          Pointer dwUser, int nSequence, Pointer reserved){
            if (lAnalyzerHandle.longValue() == 0 || pAlarmInfo == null) {
                return -1;
            }

            File path = new File("./GateSnapPicture/");
            if (!path.exists()) {
                path.mkdir();
            }

            ///< 门禁事件
            if(dwAlarmType == NetSDKLib.EVENT_IVS_ACCESS_CTL) {
                NetSDKLib.DEV_EVENT_ACCESS_CTL_INFO msg = new NetSDKLib.DEV_EVENT_ACCESS_CTL_INFO();
                ToolKits.GetPointerData(pAlarmInfo, msg);

                // 保存图片，获取图片缓存
                String snapPicPath = path + "\\" + System.currentTimeMillis() + "GateSnapPicture.jpg";  // 保存图片地址
                byte[] buffer = pBuffer.getByteArray(0, dwBufSize);
                ByteArrayInputStream byteArrInputGlobal = new ByteArrayInputStream(buffer);

                try {
                    gateBufferedImage = ImageIO.read(byteArrInputGlobal);
                    if(gateBufferedImage != null) {
                        ImageIO.write(gateBufferedImage, "jpg", new File(snapPicPath));
                    }
                } catch (IOException e2) {
                    log.error("保存门禁事件图片失败:{}", e2.getMessage());
                }

                // 图片以及门禁信息数据处理 TODO

            }
            return 0;
        }
    }

    /**
     * 解析门禁事件数据
     * @param devEventAccessCtlInfo
     * @return
     */
    private GateEventInfoVo parseData(NetSDKLib.DEV_EVENT_ACCESS_CTL_INFO devEventAccessCtlInfo) {
        GateEventInfoVo gateEventInfoVo = new GateEventInfoVo();
        // 时间
        if(devEventAccessCtlInfo.UTC == null || devEventAccessCtlInfo.UTC.toString().isEmpty()) {
            gateEventInfoVo.setTime("");
        } else {
            gateEventInfoVo.setTime(devEventAccessCtlInfo.UTC.toString());
        }
        // 开门状态
        if(devEventAccessCtlInfo.bStatus == 1) {
            gateEventInfoVo.setOpenStatus(Res.string().getSucceed());
        } else {
            gateEventInfoVo.setOpenStatus(Res.string().getFailed());
        }
        // 开门方式
        gateEventInfoVo.setOpenMethod(Res.string().getOpenMethods(devEventAccessCtlInfo.emOpenMethod));
        // 卡名
        try {
            gateEventInfoVo.setCardName(new String(devEventAccessCtlInfo.szCardName, "GBK").trim());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 卡号
        gateEventInfoVo.setCardNo(new String(devEventAccessCtlInfo.szCardNo).trim());
        // 用户ID
        gateEventInfoVo.setUserId(new String(devEventAccessCtlInfo.szUserID).trim());
        return gateEventInfoVo;
    }

    /**
     * 查询卡的信息
     */
    public List<CardInfoVo> findCardInfo(String cardNo) {
        int nFindCount = 10;
        // 卡号：  为空，查询所有的卡信息
        // 获取查询句柄
        if(!GateModule.findCard(cardNo)) {
            return null;
        }
        List<CardInfoVo> dataList = new ArrayList<>();
        // 查询具体信息
        while(true) {
            NetSDKLib.NET_RECORDSET_ACCESS_CTL_CARD[] pstRecord = GateModule.findNextCard(nFindCount);
            if(pstRecord == null) {
                break;
            }

            // 组装数据
            for(int i = 0; i < pstRecord.length; i++) {
                CardInfoVo vo = new CardInfoVo();
                vo.setCardNo(new String(pstRecord[i].szCardNo).trim());   		 // 卡号
                try {
                    vo.setCardName(new String(pstRecord[i].szCardName, "GBK").trim());   // 卡名
                } catch (UnsupportedEncodingException e) {}
                vo.setRecordNo(pstRecord[i].nRecNo);   				 // 记录集编号
                vo.setUserId(new String(pstRecord[i].szUserID).trim());   		 // 用户ID
                vo.setPassword(new String(pstRecord[i].szPsw).trim());   			 // 卡密码
                vo.setStatus(Res.string().getCardStatus(pstRecord[i].emStatus));   // 卡状态
                vo.setType(Res.string().getCardType(pstRecord[i].emType));   	 // 卡类型
                vo.setUseTime(String.valueOf(pstRecord[i].nUserTime));   	 		 // 使用次数
                vo.setFirstEnter(pstRecord[i].bFirstEnter == 1 ? Res.string().getFirstEnter() : Res.string().getNoFirstEnter());  // 是否首卡
                vo.setValid(pstRecord[i].bIsValid == 1? Res.string().getValid() : Res.string().getInValid());   	 			// 是否有效
                vo.setStartTime(pstRecord[i].stuValidStartTime.toStringTimeEx());   	 // 有效开始时间
                vo.setEndTime(pstRecord[i].stuValidEndTime.toStringTimeEx());   	 // 有效结束时间
                dataList.add(vo);
            }

            if (pstRecord.length < nFindCount) {
                break;
            }
        }
        // 关闭查询接口
        GateModule.findCardClose();
        return dataList;
    }

    /**
     * 添加卡
     * @param cardNo  	  卡号
     * @param userId  	  用户ID
     * @param cardName   卡名
     * @param cardPwd    卡密码
     * @param cardStatus 卡状态
     * @param cardType   卡类型
     * @param useTimes   使用次数
     * @param isFirstEnter  是否首卡, 1-true, 0-false
     * @param isValid  		是否有效, 1-true, 0-false
     * @param startValidTime  有效开始时间
     * @param endValidTime    有效结束时间
     * @return true:成功   false:失败
     */
    public boolean insertCard(String cardNo, String userId, String picPath,String cardName,
                                     String cardPwd,int cardStatus, int cardType, int useTimes, int isFirstEnter,
                                     int isValid, String startValidTime, String endValidTime){
        if(StringUtils.isEmpty(cardNo) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(picPath)){
            throw new BusinessException(ExceptionEnum.PARAM_ERROR);
        }
        // 其他字段长度校验 TODO

        boolean success = GateModule.insertCard(cardNo, userId, cardName, cardPwd, cardStatus, cardType, useTimes, isFirstEnter, isValid, startValidTime, endValidTime);
        if(success){
            GateModule.addFaceInfo(userId,ToolKits.readPictureFile(picPath));
        }
        return success;
    }

    /**
     * 修改卡信息
     * @param recordNo   记录集编号
     * @param cardNo  	  卡号
     * @param userId  	  用户ID
     * @param cardName   卡名
     * @param cardPwd    卡密码
     * @param cardStatus 卡状态
     * @param cardType   卡类型
     * @param useTimes   使用次数
     * @param isFirstEnter  是否首卡, 1-true, 0-false
     * @param isValid  		是否有效, 1-true, 0-false
     * @param startValidTime  有效开始时间
     * @param endValidTime    有效结束时间
     * @return true:成功   false:失败
     */
    public boolean modifyCard(int recordNo, String cardNo, String picPath,String userId, String cardName, String cardPwd,
                                     int cardStatus, int cardType, int useTimes, int isFirstEnter,
                                     int isValid, String startValidTime, String endValidTime){
        boolean success = GateModule.modifyCard(recordNo, cardNo, userId, cardName, cardPwd, cardStatus, cardType, useTimes, isFirstEnter, isValid, startValidTime, endValidTime);
        if(success){
            GateModule.modifyFaceInfo(userId,ToolKits.readPictureFile(picPath));
        }
        return success;
    }

    /**
     * 删除卡信息(单个删除)
     * @param userId 用户ID
     * @param recordNo 记录集编号
     */
    public boolean deleteCard(String userId,int recordNo){
        return GateModule.deleteFaceInfo(userId) && GateModule.deleteCard(recordNo);
    }

    /**
     * 清除所有卡信息
     */
    public boolean clearCard(){
        return GateModule.clearFaceInfo() && GateModule.clearCard();
    }
}
