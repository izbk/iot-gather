package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

/**
 * 人脸识别数据
 * @author Binke Zhang
 * @date 2020/2/17 10:31
 */
@Data
public class FaceRecognitionInfoVo {
    private FaceVo faceVo;
    private CandidateVo candidateVo;
}
