# TMProject (Today Menu)

---
## 프로젝트 개요


### 프로젝트 소개
"오늘 뭐 먹지?"라는 질문은 모두에게 공통적인 고민일 것입니다. 이런 고민을 해결하기 위한 방법 중 하나가 저희의 웹사이트에 오늘의 메뉴를 공유하는 것입니다. 이를 통해 저희는 매일 무엇을 먹었는지, 어떤 식사를 즐겼는지를 서로 공유할 수 있습니다.<br>
이러한 공유는 단순히 메뉴 선택에 대한 고민을 덜어주는 것뿐만아니라 뭘 먹는지를 공유함으로써, 우리는 서로의 일상을 이해하고, 다른 사람들의 식사 스타일을 알수있습니다.<br>
그러므로 저희의 웹사이트에 매일 우리의 식사를 공유하는 것은 단순히 '오늘 뭐 먹지?'라는 고민을 해결하는 것 이상의 가치를 가지고 있습니다. 그것은 우리의 식사로서 일상을 공유하고 그 사람에 대한 이해도 높일수 있는 그러한 SNS서비스가 될수있습니다.


### 기능

---
- [ ]  **사용자 인증 기능**
    - 회원가입 기능
        - 계정정보 규칙 적용
    - 로그인 및 로그아웃 기능
        - JWT 활용
        - 사용자는 자신의 계정으로 서비스에 로그인하고 로그아웃 가능
- [ ]  **계정 관리**
    - 프로필 수정 기능
        - 이름, 한 줄 소개와 같은 기본적인 정보 수정
        - 비밀번호 규칙 적용
- [ ]  **게시물 CRUD 기능**
    - 게시물 작성, 조회, 수정, 삭제 기능
        - 게시물 조회를 제외한 나머지 기능들은 전부 인가(Authorization) 개념이 적용되어야 하며 이는 JWT와 같은 토큰으로 검증 가능.
        - 오로지 본인만 게시글 수정/삭제 가능.

- [ ]  **댓글 CRUD 기능**
    - 댓글 작성, 조회, 수정, 삭제 기능
        - 댓글 조회를 제외한 나머지 기능들은 전부 인가(Authorization) 개념이 적용되어야 하며 이는 JWT와 같은 토큰으로 검증 가능.
        - 오로지 본인만 댓글 수정/삭제 가능.
- [ ]  **좋아요 기능**
    - 게시물 및 댓글 좋아요/좋아요 취소 기능
        - 사용자가 게시물이나 댓글에 좋아요를 남기거나 취소할 수 있어야 합니다.
        - 이 때, 본인이 작성한 게시물과 댓글에 좋아요는 남길 수 없도록 해본다.

- [ ]  **팔로우 기능 구현**
    - 특정 사용자를 팔로우/언팔로우를 할 수 있으면 너무 좋습니다.
    - 팔로우 기능이 구현되었다면 팔로우하는 사용자의 게시물을 볼 수 있어야 한다
- --
### 기술스택
- ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) JAR 17
-  ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
- Spring Boot 3.1.5
- Spring Security 6
- JPA/Hibernate
- ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white) 8.5
- ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
- ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
- ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
- 	![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)
- 	![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)

## 구현 방법
이 프로젝트는 Java와 Spring, Spring Security, 그리고 Gradle을 이용하여 구현되었다.<br> 사용자 인증은 Spring Security를 이용하였으며, 데이터베이스 연동은 Spring Data JPA를 이용하였다. <br>또한, Gradle을 이용하여 프로젝트의 의존성을 관리하였다.

---
### 역할 담당
- 진유록

  **게시물 CRUD 기능,댓글 CRUD 기능**

- 문정현

  **좋아요 기능,팔로우 기능**

- 박지환

  **사용자 인증 기능,계정 관리**

### 팀소개

#### 6시에 밥먹조?

##### 팀 이름이 6시엔 밥먹조???
1. 일정한 시간에 식사를 하는 것은 생활 패턴의 규칙성을 의미하죠.  
   팀의 업무도 일정하고 규칙적으로 진행됨을 상징합니다!

2. '밥 먹는 시간’을 정해둠으로서 팀원들이 함께 정확한 시간적인 소통하는 것입니다!
   (ex:6시에 저녁 맛있게드세요 라며 일상적인 대화를 할수있음을 의미합니다).
   팀워크의 중요성과 서로간의 소통을 강조합니다.

3. '6시'라는 시간은 일반적으로 저녁전의 하루 마무리 시간으로 봅니다.
   그렇다면  하루를 마무리 짓는 중요한 시간을 정해둠으로서
   어느정도의 계획을 마무리했는지 돌아보며 남은 할일들을 생각하는 시간을 가질수 있습니다.

4. '빠르게 많은 걸 하자'는 목표에 따라,
   '6시엔 밥먹조'는 효율적이고 빠르게 일을 처리하여
   6시에는 모두가 밥을 먹을 수 있는 시간이 되도록 하자는 의미이기도 합니다.

5. 밥이라는 것은 우리의 일상에 중요한 부분이기에
   서로에게 도움이 되고 서로에게 중요한 사람이 되자는 의미를 가지고있습니다.

   따라서 이 이름은 "규칙적이고 효율적인 업무 진행",
   "팀워크와 소통의 중요성",
   "하루를 마무리 지으며 하지 못한 일들을 생각하며 수행" ,
   "서로에게 의미있는 팀원이 되자"  라는 많은 의미를 담은 팀명입니다!
   절대 가볍게 지은것이 아니란 말입니다!

---
## API 명세서

---
### 사용자 API
#### 회원가입
- URL: `/api/member/signup`
- Method: `POST`
- Request Body: `{"username": "username", "password": "password"}`
- Response: `{"msg": "로그인 성공", "statusCode": 200}`

#### 로그인
- URL: `/api/member/login`
- Method: `POST`
- Request Body: `{"username": "username", "password": "password", "passwordConfirm": "passwordConfirm", "email": "email"}`
- Response: `{"msg": "회원가입 성공", "statusCode": 200}`

#### 로그 아웃
- URL: `/api/member/logout`
- Method: `GET`

#### 프로필 수정
- URL: `/api/member/profile/{memberId}`
- Method: `PUT`
- Authorization: `Bearer`
- Request Body: `{"username": "username", "password": "password", "passwordConfirm": "passwordConfirm", "email": "email", "introduction": "introduction"}`
- Response: `ProfileResponseDto, {"msg": "회원 프로필 수정 성공", "statusCode": 200}`

---
### 게시물 API
#### 게시물 작성
- URL: `/api/member/boards`
- Method: `POST`
- Authorization: `Bearer`
- Request Body: `{"title": "title", "content": "content", "boardUsername": "boardUsername"}`
- Response: `BoardResponseDto, {"msg": "게시글 작성 성공", "statusCode": 201}`

#### 게시물 조회
- URL: `/api/member/boards/{boardId}`
- Method: `GET`
- Response: `BoardListDto, {"msg": "게시글 조회 성공", "statusCode": 200}`

#### 게시물 수정
- URL: `/api/member/boards/{boardId}`
- Method: `PUT`
- Authorization: `Bearer`
- Request Body: `{"title": "title", "content": "content", "boardUsername": "boardUsername"}`
- Response: `BoardResponseDto, {"msg": "게시글 수정 성공", "statusCode": 200}`

#### 게시물 삭제
- URL: `/api/member/boards/{boardId}`
- Method: `DELETE`
- Authorization: `Bearer`
- Response: `{"msg": "게시글 삭제성공", "statusCode": 200}`

---
### 댓글 API
#### 댓글 작성
- URL: `/api/member/comments`
- Method: `POST`
- Authorization: `Bearer`
- Request Body: `{"content": "content"}`
- Response: `CommentResponseDto, {"msg": "댓글 작성 성공", "statusCode": 200}`

#### 댓글 조회
- URL: `/api/member/comments/{commentId}`
- Method: `GET`
- Response: `CommentListDto, {"msg": "댓글 조회 성공", "statusCode": 200}`

#### 댓글 수정
- URL: `/api/member/comments/{commentId}`
- Method: `UPDATE`
- Authorization: `Bearer`
- Response: `CommentResponseDto, {"msg": "댓글 수정 성공", "statusCode": 200}`

#### 댓글 삭제
- URL: `/api/member/comments/{commentId}`
- Method: `DELETE`
- Authorization: `Bearer`
- Response: `{"msg": "댓글 삭제성공", "statusCode": 200}`

---
### 좋아요 API
#### 댓글 좋아요
- URL: `/api/member/comments/{commentId}/like`
- Method: `POST`
- Authorization: `Bearer`
- Response: `{"msg": "댓글 좋아요 성공", "statusCode": 201}`

#### 댓글 좋아요 취소
- URL: `/api/member/comments/{commentId}/like`
- Method: `DELETE`
- Authorization: `Bearer`
- Response: `{"msg": "댓글 좋아요 취소 성공", "statusCode": 200}`

#### 게시글 좋아요
- URL: `/api/member/board/{boardId}/likes`
- Method: `POST`
- Authorization: `Bearer`
- Response: `{"msg": "게시글 좋아요 성공", "statusCode": 201}`

#### 게시글 좋아요 취소
- URL: `/api/member/board/{boardId}/likes`
- Method: `DELETE`
- Authorization: `Bearer`
- Response: `{"msg": "게시글 좋아요 취소 성공", "statusCode": 200}`

---
### 팔로우 API
#### 팔로우
- URL: `/api/member/follow/{followId}`
- Method: `POST`
- Authorization: `Bearer`
- Response: `{"msg": "팔로우 성공", "statusCode": 201}`

#### 팔로우 취소
- URL: `/api/member/follow/{followId}`
- Method: `DELETE`
- Authorization: `Bearer`
- Response: `{"msg": "팔로우 취소 성공", "statusCode": 200}`


---
## ERD
![스크린샷 2023-12-08 오후 4.36.54.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202023-12-08%20%EC%98%A4%ED%9B%84%204.36.54.png)
---

## 디렉토리 구조
```asql
 src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── tmproject
    │   │           ├── Common
    │   │           │   ├── Jwt
    │   │           │   │   ├── JwtAuthenticationFilter.java
    │   │           │   │   ├── JwtAuthorizationFilter.java
    │   │           │   │   └── JwtUtil.java
    │   │           │   ├── Security
    │   │           │   │   ├── MemberDetailsImpl.java
    │   │           │   │   ├── MemberDetailsServiceImpl.java
    │   │           │   │   └── WebSecurityConfig.java
    │   │           │   └── config
    │   │           │       ├── JpaConfig.java
    │   │           │       └── RestTemplateConfig.java
    │   │           ├── TmProjectApplication.java
    │   │           ├── api
    │   │           │   ├── board
    │   │           │   │   ├── controller
    │   │           │   │   │   └── BoardController.java
    │   │           │   │   ├── dto
    │   │           │   │   │   ├── BoardListDto.java
    │   │           │   │   │   ├── BoardRequestDto.java
    │   │           │   │   │   ├── BoardResponseDto.java
    │   │           │   │   │   └── BoardViewResponseDto.java
    │   │           │   │   ├── entity
    │   │           │   │   │   └── Board.java
    │   │           │   │   ├── exception
    │   │           │   │   │   └── BoardNotFoundException.java
    │   │           │   │   ├── repository
    │   │           │   │   │   └── BoardRepository.java
    │   │           │   │   └── service
    │   │           │   │       └── BoardService.java
    │   │           │   ├── comment
    │   │           │   │   ├── controller
    │   │           │   │   │   └── CommentController.java
    │   │           │   │   ├── dto
    │   │           │   │   │   ├── CommentListDto.java
    │   │           │   │   │   ├── CommentRequestDto.java
    │   │           │   │   │   └── CommentResponseDto.java
    │   │           │   │   ├── entity
    │   │           │   │   │   └── Comment.java
    │   │           │   │   ├── exception
    │   │           │   │   │   └── CommentNotFoundException.java
    │   │           │   │   ├── repository
    │   │           │   │   │   └── CommentRepository.java
    │   │           │   │   └── service
    │   │           │   │       └── CommentService.java
    │   │           │   ├── follow
    │   │           │   │   ├── controller
    │   │           │   │   │   └── FollowController.java
    │   │           │   │   ├── entity
    │   │           │   │   │   └── Follow.java
    │   │           │   │   ├── repository
    │   │           │   │   │   └── FollowRepository.java
    │   │           │   │   └── service
    │   │           │   │       └── FollowService.java
    │   │           │   ├── like
    │   │           │   │   ├── controller
    │   │           │   │   │   └── likeController.java
    │   │           │   │   ├── dto
    │   │           │   │   │   ├── LikeBoard.java
    │   │           │   │   │   ├── LikeComment.java
    │   │           │   │   │   ├── requestDto
    │   │           │   │   │   │   └── LikeCommemtRequestDto.java
    │   │           │   │   │   └── responseDto
    │   │           │   │   │       └── LikeBoardResponseDto.java
    │   │           │   │   ├── entity
    │   │           │   │   │   └── Like.java
    │   │           │   │   ├── repository
    │   │           │   │   │   └── LikeRepository.java
    │   │           │   │   └── service
    │   │           │   │       └── LikeService.java
    │   │           │   └── member
    │   │           │       ├── controller
    │   │           │       │   ├── MemberController.java
    │   │           │       │   └── OAuthController.java
    │   │           │       ├── dto
    │   │           │       │   ├── GoogleMemberInfoDto.java
    │   │           │       │   ├── KakaoMemberInfoDto.java
    │   │           │       │   ├── LoginRequestDto.java
    │   │           │       │   ├── NaverMemberInfoDto.java
    │   │           │       │   ├── ProfileResponseDto.java
    │   │           │       │   ├── ProfileUpdateRequestDto.java
    │   │           │       │   └── SignupRequestDto.java
    │   │           │       ├── entity
    │   │           │       │   ├── Member.java
    │   │           │       │   ├── MemberRoleEnum.java
    │   │           │       │   └── OauthEnum.java
    │   │           │       ├── repository
    │   │           │       │   └── MemberRepository.java
    │   │           │       └── service
    │   │           │           ├── MemberService.java
    │   │           │           └── OAuthService.java
    │   │           ├── global
    │   │           │   ├── common
    │   │           │   │   ├── ApiResponseDto.java
    │   │           │   │   └── Timestamped.java
    │   │           │   └── config
    │   │           └── homeController.java
    │   └── resources
    │       ├── application.yml
    │       ├── static
    │       │   ├── css
    │       │   │   ├── board.css
    │       │   │   ├── create.css
    │       │   │   ├── mypage.css
    │       │   │   ├── profile.css
    │       │   │   └── stylees.css
    │       │   ├── images
    │       │   └── js
    │       │       └── board.js
    │       └── templates
    │           ├── board.html
    │           ├── create.html
    │           ├── index.html
    │           ├── indexBefore.html
    │           ├── mypage.html
    │           ├── oauthLogin.html
    │           ├── profile.html
    │           └── signup.html
    └── test
        └── java
            └── com
                └── tmproject
                    ├── TmProjectApplicationTests.java
                    └── api
                        ├── board
                        │   ├── BoardControllerIntegrationTest.java
                        │   ├── controller
                        │   │   └── BoardControllerTest.java
                        │   ├── entity
                        │   │   └── BoardTest.java
                        │   └── service
                        │       └── BoardServiceTest.java
                        ├── comment
                        │   ├── controller
                        │   │   └── CommentControllerTest.java
                        │   └── service
                        │       └── CommentServiceTest.java
                        ├── common
                        │   └── JwtUtilTest.java
                        └── member
                            ├── controller
                            │   ├── MemberControllerTest.java
                            │   └── OAuthControllerTest.java
                            └── service
                                └── MemberServiceTest.java
                                
```