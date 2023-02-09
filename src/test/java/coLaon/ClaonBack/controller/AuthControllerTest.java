package coLaon.ClaonBack.controller;

import coLaon.ClaonBack.common.domain.JwtDto;
import coLaon.ClaonBack.common.utils.HeaderUtil;
import coLaon.ClaonBack.common.utils.JwtUtil;
import coLaon.ClaonBack.common.utils.RefreshTokenUtil;
import coLaon.ClaonBack.config.JwtAuthenticationEntryPoint;
import coLaon.ClaonBack.config.JwtAuthenticationFilter;
import coLaon.ClaonBack.config.WebSecurityConfig;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.domain.UserDetails;
import coLaon.ClaonBack.user.dto.DuplicatedCheckResponseDto;
import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.UserService;
import coLaon.ClaonBack.user.web.AuthController;
import coLaon.ClaonBack.user.web.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletException;

import java.util.ArrayList;
import java.util.Optional;

import static coLaon.ClaonBack.controller.utils.SpringRestDocsUtils.getDocumentRequest;
import static coLaon.ClaonBack.controller.utils.SpringRestDocsUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
//@WebMvcTest(value = AuthController.class, includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
public class AuthControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private HeaderUtil headerUtil;
//
//    @MockBean
//    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

//    @MockBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

//    @MockBean
//    private JwtUtil jwtUtil;
//
//    @MockBean
//    private RefreshTokenUtil refreshTokenUtil;
//
//    @MockBean
//    private UserRepository userRepository;

    private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private User user;
    private UserDetails userDetails;
    private SignUpRequestDto signUpRequestDto;
    private UserResponseDto userResponseDto;
    private DuplicatedCheckResponseDto duplicatedCheckResponseDto;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) throws ServletException {
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
//                .addFilters(new JwtAuthenticationFilter())
                .apply(documentationConfiguration(restDocumentation))
                .build();

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
        ReflectionTestUtils.setField(this.user, "id", "userId");

        this.userDetails = new UserDetails(this.user);

        this.signUpRequestDto = new SignUpRequestDto(
                "user",
                175.0F,
                178.0F,
                "",
                "123456",
                "test"
        );

        this.userResponseDto = UserResponseDto.from(this.user);
        this.duplicatedCheckResponseDto = DuplicatedCheckResponseDto.of(true);


    }

    @Test
    public void duplicateCheckWhenTrueShouldSuccess() throws Exception {
        // given
        String nickname = "TEST_NICKNAME";
        given(this.userService.nicknameDuplicatedCheck(nickname)).willReturn(this.duplicatedCheckResponseDto);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                get("/api/v1/auth/nickname/{nickname}/duplicate-check", nickname)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(this.duplicatedCheckResponseDto)))
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
//    @WithMockUser(username = "user")
    public void successSignUp() throws Exception {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(this.user));
        given(this.userService.signUp(this.userDetails.getUser(), this.signUpRequestDto)).willReturn(this.userResponseDto);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/v1/auth/sign-up")
                        .header("isCompletedSignUp", Boolean.TRUE.toString())
//                        .with(user(this.userDetails))
//                        .param("userDetails", objectMapper.writeValueAsString(this.userDetails))
                        .content(objectMapper.writeValueAsString(this.signUpRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(this.userResponseDto)))
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
                                fieldWithPath("height").type(JsonFieldType.STRING).description("신장"),
                                fieldWithPath("armReach").type(JsonFieldType.STRING).description("암리치"),
                                fieldWithPath("apeIndex").type(JsonFieldType.STRING).description("팔길이비율"),
                                fieldWithPath("imagePath").type(JsonFieldType.STRING).description("이미지주소"),
                                fieldWithPath("instagramOAuthId").type(JsonFieldType.STRING).description("인스타그램 OAuthId"),
                                fieldWithPath("instagramUserName").type(JsonFieldType.STRING).description("인스타그램 아이디"),
                                fieldWithPath("isPrivate").type(JsonFieldType.STRING).description("계정공개여부")
                        )
                ));
    }

}
