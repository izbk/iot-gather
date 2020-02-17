package net.cdsunrise.ztyg.acquisition.vrv.domain.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 设备新增参数
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
public class DeviceRequest {
        /**
         * 新增参数
         */
        @Data
        public static class AddReq{
                /** id */
                @NotNull(message = "id不能为空")
                private Long id ;
        }
        /**
         * 更新参数
         */
        @Data
        public static class UpdateReq{
                /** id */
                @NotNull(message = "id不能为空")
                private Long id ;
        }
        /**
         * 删除参数
         */
        @Data
        public static class DeleteReq{
                /** id */
                @NotNull(message = "id不能为空")
                private Long id ;
        }
        /**
         * 查询参数
         */
        @Data
        public static class QueryReq {
                /** id */
                @NotNull(message = "id不能为空")
                private Long id ;
        }


}
