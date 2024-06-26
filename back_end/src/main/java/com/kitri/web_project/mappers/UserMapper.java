package com.kitri.web_project.mappers;

import com.kitri.web_project.dto.*;
import com.kitri.web_project.dto.api.BookMarks;
import com.kitri.web_project.dto.diary.DiaryImgDto;
import com.kitri.web_project.dto.diary.DiaryMainImg;
import com.kitri.web_project.dto.diary.RequestDiary;
import com.kitri.web_project.dto.diary.PetCalendar;
import com.kitri.web_project.dto.login.ResponseClient;
import com.kitri.web_project.dto.login.ResponseUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    Long nameCheck(String name);
    ResponseUser findMember(String name, String email);
    void signup(String email, String name, String password, String address, String imgPath, boolean social);
    ResponseClient findByEmail(String email, boolean social);
    UserInfo findById(long id);
    List<PetInfo> getPets(long id);
    List<DiaryInfo> getDiary(long id);
    void save(RequestDiary diaryInfo); //펫 다이어리 등록
    RequestDiary petDiary(long id); //펫 다이어리 미리보기
    void deleteDiary(long diaryId); //다이어리 삭제하기
    List<PetCalendar> petCalendar(long id); //캘린더 미리보기
    void updateUser(UserUpdateInfo userUpdateInfo);
    String getUserImages(long id);
    List<RequestDiary>UpdateDiary(long id); //다이어리 수정 불러오기
    void editDiary(RequestDiary id); //다이어리 수정하기
    void deleteUser(Long id);
    void updatePassword(String password, long id);
    void updateNewPassword(List<Object> params);

    void imageSave(String imgPath, long userId, long petId, long id);

    List<DiaryImgDto> getDiaryImages(long id);
    void deleteImageById(@Param("id") long id);

    List<DiaryMainImg> diaryMainImages(long id, int page);

    void UpdateColor(long petId, String color);

    String passwordFind(long id);

    int getMaxPage(long id);

    String duplicateCheck(long id, String name);

    String checkEmail(String email);

    void updateEmail(String email, long id);
}
