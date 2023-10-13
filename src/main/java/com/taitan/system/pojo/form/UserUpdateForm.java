package com.taitan.system.pojo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;


/**
 * 用户表单对象
 *
 * @author haoxr
 * @date 2022/4/12 11:04
 */
@Schema(description = "用户表单更新对象")
@Data
public class UserUpdateForm {

    @Schema(description="用户ID")
    private Long id;

    @Schema(description="用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "手机号格式不对")
    private String username;

    @Schema(description="昵称")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "手机号格式不对")
    private String mobile;

    @Schema(description="性别")
    private Integer gender;

    @Schema(description="用户头像")
    private String avatar;

    @Schema(description="邮箱")
    private String email;

    @Schema(description="生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description="职业")
    private String occupation;

    @Schema(description="职称")
    private String profession;

}
