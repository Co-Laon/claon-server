package coLaon.ClaonBack.controller;

import coLaon.ClaonBack.common.utils.HeaderUtil;
import coLaon.ClaonBack.controller.utils.UserDetailsArgumentResolver;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.domain.UserDetails;
import coLaon.ClaonBack.user.dto.DuplicatedCheckResponseDto;
import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.service.UserService;
import coLaon.ClaonBack.user.web.AuthController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.ServletException;

import static coLaon.ClaonBack.controller.utils.SpringRestDocsUtils.getDocumentRequest;
import static coLaon.ClaonBack.controller.utils.SpringRestDocsUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
public class AuthControllerTest {
    private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;
    @Mock
    private HeaderUtil headerUtil;

    @InjectMocks
    private AuthController authController;

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) throws ServletException {
        this.user = User.of(
                "user@gmail.com",
                "1234567890",
                "user",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(user, "id", "userId");

        this.mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .setCustomArgumentResolvers(new UserDetailsArgumentResolver(this.user))
                .apply(documentationConfiguration(restDocumentation))
                .build();

        this.userDetails = new UserDetails(this.user);
    }

    @Test
    public void duplicateCheckWhenTrueShouldSuccess() throws Exception {
        // given
        DuplicatedCheckResponseDto duplicatedCheckResponseDto = DuplicatedCheckResponseDto.of(true);
        String nickname = "TEST_NICKNAME";
        given(this.userService.nicknameDuplicatedCheck(nickname)).willReturn(duplicatedCheckResponseDto);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                get("/api/v1/auth/nickname/{nickname}/duplicate-check", nickname)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(duplicatedCheckResponseDto)))
                .andDo(print())
                .andDo(document(
                        "nickname-duplicate",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.BOOLEAN).description("중복여부")
                        )
                ));
    }

    @Test
    public void successSignUp() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                "user",
                175.0F,
                178.0F,
                "",
                "123456",
                "test"
        );
        UserResponseDto userResponseDto = UserResponseDto.from(this.user);

        given(this.userService.signUp(this.userDetails.getUser(), signUpRequestDto)).willReturn(userResponseDto);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/v1/auth/sign-up")
                        .header("isCompletedSignUp", Boolean.TRUE.toString())
                        .content(objectMapper.writeValueAsString(signUpRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(userResponseDto)))
                .andDo(document(
                        "sign-up",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("height").type(JsonFieldType.NUMBER).description("신장"),
                                fieldWithPath("armReach").type(JsonFieldType.NUMBER).description("암리치"),
                                fieldWithPath("imagePath").type(JsonFieldType.STRING).description("이미지주소"),
                                fieldWithPath("instagramOAuthId").type(JsonFieldType.STRING).description("인스타그램 OAuthId"),
                                fieldWithPath("instagramUserName").type(JsonFieldType.STRING).description("인스타그램 아이디")
                        ),
                        responseFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("height").type(JsonFieldType.NUMBER).description("신장"),
                                fieldWithPath("armReach").type(JsonFieldType.NUMBER).description("암리치"),
                                fieldWithPath("apeIndex").type(JsonFieldType.NUMBER).description("팔길이비율"),
                                fieldWithPath("imagePath").type(JsonFieldType.STRING).description("이미지주소"),
                                fieldWithPath("instagramOAuthId").type(JsonFieldType.STRING).description("인스타그램 OAuthId"),
                                fieldWithPath("instagramUserName").type(JsonFieldType.STRING).description("인스타그램 아이디"),
                                fieldWithPath("isPrivate").type(JsonFieldType.BOOLEAN).description("계정공개여부")
                        )
                ));
    }
}
