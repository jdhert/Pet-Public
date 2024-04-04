package com.kitri.web_project.service;

import com.kitri.web_project.dto.UserUpdateInfo;
import com.kitri.web_project.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class UserInfoService {
    @Autowired
    UserMapper userMapper;

    private static final String frontendUrl = "http://localhost:3000";
//  private static final String frontendUrl = System.getenv("FRONTEND_URL");

    private static final String uploadRootPath = "D:/imageStore";
    //    private static final String uploadRootPath = "/app/images";

    public boolean passwordVerify(long id, String password){
        String storedPassword = userMapper.passwordFind(id);
        if (storedPassword == null)
            return false;
        return Objects.equals(password, storedPassword);
    }

    public void userUpdate(UserUpdateInfo userUpdateInfo){
        String imgPath = userMapper.getUserImages(userUpdateInfo.getUserId());

        //유저 닉네임이랑 이메일 변경시 중복 안되는지 먼저 확인

        userMapper.updateUser(userUpdateInfo);
        String fullPath = uploadRootPath + imgPath;
        File file = new File(fullPath);
        if (file.exists()) {
            try {
                if (file.delete()) {
                    System.out.println("Success: Image deleted");
                } else {
                    System.out.println("Failed: Image could not be deleted");
                }
            } catch (SecurityException e) {
                System.out.println("Failed: Security Exception occurred while deleting image");
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed: Image not found at path " + fullPath);
        }
    }

    public boolean duplicationCheck(long id, String name){
        return userMapper.duplicateCheck(id, name) == null;
    }

    public boolean checkEmail(String email){
        return  userMapper.checkEmail(email) == null;
    }
}
