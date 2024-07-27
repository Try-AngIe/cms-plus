package kr.or.kosa.cmsplusmain.domain.product.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import kr.or.kosa.cmsplusmain.RandomGenerator;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductCreateReq;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductDetailRes;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductUpdateReq;
import kr.or.kosa.cmsplusmain.domain.product.service.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ProductControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockBean
   private ProductService productService;

   private ObjectMapper objectMapper;

   private final Random random = new Random();
   private final RandomGenerator randomGenerator = new RandomGenerator(random);

   @BeforeEach
   public void setUp(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
       this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
               .apply(documentationConfiguration(restDocumentation)
                       .operationPreprocessors()
                       .withRequestDefaults(prettyPrint())
                       .withResponseDefaults(prettyPrint())
               )
               .alwaysDo(MockMvcResultHandlers.print())
               .addFilters(new CharacterEncodingFilter("UTF-8", true))
               .build();

       this.objectMapper = new ObjectMapper();
       this.objectMapper.registerModule(new JavaTimeModule());
       this.objectMapper.registerModule(new Jdk8Module());
       this.objectMapper.registerModule(new ParameterNamesModule());
       this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
   }

   @Test
   public void testCreateProduct() throws Exception {
       ProductCreateReq productReq = new ProductCreateReq();

       Field productNameField = ProductCreateReq.class.getDeclaredField("productName");
       productNameField.setAccessible(true);
       productNameField.set(productReq, "테스트 상품");

       Field productPriceField = ProductCreateReq.class.getDeclaredField("productPrice");
       productPriceField.setAccessible(true);
       productPriceField.set(productReq, 10000);

       Field productMemoField = ProductCreateReq.class.getDeclaredField("productMemo");
       productMemoField.setAccessible(true);
       productMemoField.set(productReq, "테스트 상품 메모");

       mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/vendor/product")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(productReq)))
               .andExpect(status().isOk())
               .andDo(document("product-create",
                       requestFields(
                               fieldWithPath("productName").description("상품 이름"),
                               fieldWithPath("productPrice").description("상품 가격"),
                               fieldWithPath("productMemo").description("상품 메모")
                       )
               ));
   }


   @Test
   public void testGetProductDetail() throws Exception {
       Long productId = 1L;

       ProductDetailRes productDetailRes = ProductDetailRes.builder()
               .productId(productId)
               .productName("테스트 상품")
               .productPrice(10000)
               .contractNumber(5)
               .productCreatedDate(LocalDate.now())
               .productMemo("테스트 상품 메모")
               .build();

       given(productService.getProductDetail(eq(productId), anyLong()))
               .willReturn(productDetailRes);

       mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/vendor/product/{productId}", productId))
               .andExpect(status().isOk())
               .andDo(document("product-detail",
                       pathParameters(
                               parameterWithName("productId").description("상품 ID")
                       ),
                       responseFields(
                               fieldWithPath("productId").description("상품 ID"),
                               fieldWithPath("productName").description("상품 이름"),
                               fieldWithPath("productPrice").description("상품 가격"),
                               fieldWithPath("contractNumber").description("계약 건수"),
                               fieldWithPath("productCreatedDate").description("상품 생성일"),
                               fieldWithPath("productMemo").description("상품 메모")
                       )
               ));
   }


   @Test
   public void testUpdateProduct() throws Exception {
       Long productId = 1L;
       ProductUpdateReq productUpdateReq = new ProductUpdateReq();
       Field productPriceField = ProductUpdateReq.class.getDeclaredField("productPrice");
       productPriceField.setAccessible(true);
       productPriceField.set(productUpdateReq, 20000);

       Field productMemoField = ProductUpdateReq.class.getDeclaredField("productMemo");
       productMemoField.setAccessible(true);
       productMemoField.set(productUpdateReq, "업데이트된 상품 메모");

       mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/vendor/product/{productId}", productId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(productUpdateReq)))
               .andExpect(status().isOk())
               .andDo(document("product-update",
                       pathParameters(
                               parameterWithName("productId").description("상품 ID")
                       ),
                       requestFields(
                               fieldWithPath("productPrice").description("상품 가격"),
                               fieldWithPath("productMemo").description("상품 메모")
                       )
               ));
   }


   @Test
   public void testDeleteProduct() throws Exception {
       Long productId = 1L;

       mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/vendor/product/{productId}", productId))
               .andExpect(status().isOk())
               .andDo(document("product-delete",
                       pathParameters(
                               parameterWithName("productId").description("상품 ID")
                       )
               ));
   }
}
