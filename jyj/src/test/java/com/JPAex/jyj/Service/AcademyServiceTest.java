package com.JPAex.jyj.Service;

import com.JPAex.jyj.domain.*;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AcademyServiceTest {
    @Autowired
    private AcademyRepository academyRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AcademyService academyService;

    @AfterEach
    public void cleanAll() {
        academyRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        List<Academy> academies = new ArrayList<>();
        Teacher teacher = teacherRepository.save(new Teacher("선생님"));

        Academy academy = null;
        for (int i = 0; i < 10; i++) {
            academy = Academy.builder()
                    .name("강남스쿨" + i)
                    .build();

            academy.addSubject(Subject.builder().name("자바웹개발" + i).teacher(teacher).build());
            academy.addSubject(Subject.builder().name("파이썬자동화" + i).teacher(teacher).build()); // Subject를 추가
            academies.add(academy);
        }

        academyRepository.saveAll(academies);
    }

    @Test
    public void Academy여러개를_조회시_Subject가_N1_쿼리가발생한다() throws Exception {
        //given
        List<String> subjectNames = academyService.findAllSubjectNames();

        //then
        assertEquals(subjectNames.size(),10);
    }

    @Test
    public void Academy여러개를_joinFetch로_가져온다() throws Exception {
        //given
        List<Academy> academies = academyRepository.findAllJoinFetch();
        List<String> subjectNames = academyService.findAllSubjectNamesByJoinFetch();

        //then
        assertEquals(academies.size(), 20); // 20개가 조회!?
        assertEquals(subjectNames.size(), 20); // 20개가 조회!?
    }

    @Test
    public void Academy여러개를_EntityGraph로_가져온다() throws Exception {
        //given
        List<Academy> academies = academyRepository.findAllEntityGraph();
        List<String> subjectNames = academyService.findAllSubjectNamesByEntityGraph();

        //then
        assertEquals(academies.size(), 20);
        assertEquals(subjectNames.size(), 20);
    }

    @Test
    public void Academy여러개를_distinct해서_가져온다 () throws Exception {
        //given
        System.out.println("조회 시작");
        List<Academy> academies = academyRepository.findAllJoinFetchDistinct();

        //then
        System.out.println("조회 끝");
        assertEquals(academies.size(), 10);
    }

    @Test
    public void Academy_Subject_Teacher를_한번에_가져온다() throws Exception {
        //given
        System.out.println("조회 시작");
        List<Teacher> teachers = academyRepository.findAllWithTeacher().stream()
                .map(a -> a.getSubjects().get(0).getTeacher())
                .collect(Collectors.toList());

        //then
        System.out.println("조회 끝");
        assertEquals(teachers.size(), 10);
    }

    @Test
    public void Academy_Subject_Teacher를_EntityGraph한번에_가져온다() throws Exception {
        //given
        System.out.println("조회 시작");
        List<Teacher> teachers = academyRepository.findAllEntityGraphWithTeacher().stream()
                .map(a -> a.getSubjects().get(0).getTeacher())
                .collect(Collectors.toList());

        //then
        System.out.println("조회 끝");
        assertEquals(teachers.size(), 10);
        assertEquals(teachers.get(0).getName(), "선생님");

    }
}