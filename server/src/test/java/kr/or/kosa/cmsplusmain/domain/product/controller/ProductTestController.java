//package kr.or.kosa.cmsplusmain.domain.product.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import kr.or.kosa.cmsplusmain.test.domain.product.controller.TProductController;
//import kr.or.kosa.cmsplusmain.test.domain.product.dto.TProductSaveReq;
//import kr.or.kosa.cmsplusmain.test.domain.product.dto.TProductUpdateReq;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.restdocs.RestDocumentationExtension;
//import org.springframework.restdocs.headers.HeaderDocumentation;
//import org.springframework.restdocs.payload.PayloadDocumentation;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.filter.CharacterEncodingFilter;
//
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
//
//@ExtendWith({RestDocumentationExtension.class})
//@AutoConfigureMockMvc
//@SpringBootTest
//class ProductTestController {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Autowired
//    TProductController tProductController;
//
//    // 테스트 초기화
//    @BeforeEach
//    void setUp(WebApplicationContext webApplicationContext,
//               RestDocumentationContextProvider restDocumentationContextProvider) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .addFilter(new CharacterEncodingFilter("UTF-8", true))
//                .apply(documentationConfiguration(restDocumentationContextProvider)
//                        .operationPreprocessors()
//                        .withRequestDefaults(modifyUris().host("localhost").removePort(), prettyPrint())
//                        .withResponseDefaults(modifyUris().host("localhost").removePort(), prettyPrint()))
//                .build();
//
//        tProductController.initData(); // 더미 데이터 초기화
//    }
//
//    // 상품 목록 조회
//    @Test
//    void findAllProductByCondition() throws Exception {
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/producttest")
//                        .param("page", "1")
//                        .param("offset", "10")
//                        .header(HttpHeaders.AUTHORIZATION, "ACCESS_TOKEN")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document("findAllProductByCondition",
//                        HeaderDocumentation.requestHeaders( //요청 헤더
//                                HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("ACCESS_TOKEN")
//                        ),
//                        HeaderDocumentation.responseHeaders( // 응답 헤더
//                                HeaderDocumentation.headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-Type 헤더")
//                        ),
//                        PayloadDocumentation.responseFields( // 응답 바디
//                                PayloadDocumentation.fieldWithPath("page").description("현재 페이지 번호"),
//                                PayloadDocumentation.fieldWithPath("offset").description("페이지당 항목 수"),
//                                PayloadDocumentation.fieldWithPath("totalPage").description("총 페이지 수"),
//                                PayloadDocumentation.fieldWithPath("totalCount").description("총 제품 수"),
//                                PayloadDocumentation.fieldWithPath("data[].id").description("상품번호"),
//                                PayloadDocumentation.fieldWithPath("data[].vendorId").description("고객번호"),
//                                PayloadDocumentation.fieldWithPath("data[].name").description("상품명"),
//                                PayloadDocumentation.fieldWithPath("data[].price").description("금액"),
//                                PayloadDocumentation.fieldWithPath("data[].contractCount").description("계약수"),
//                                PayloadDocumentation.fieldWithPath("data[].createdDateTime").description("생성시기"),
//                                PayloadDocumentation.fieldWithPath("data[].updatedDateTime").description("수정시기"),
//                                PayloadDocumentation.fieldWithPath("data[].deletedDate").description("삭제시기"),
//                                PayloadDocumentation.fieldWithPath("data[].memo").description("비고"),
//                                PayloadDocumentation.fieldWithPath("data[].status").description("상태")
//                        )
//                ));
//    }
//
//    // 상품 등록
//    @Test
//    void saveProduct() throws Exception {
//
//        // 등록할 상품 정보 주입
//        TProductSaveReq newProduct = new TProductSaveReq();
//        newProduct.setId(1L);
//        newProduct.setVendorId(1003L);
//        newProduct.setName("상품 등록용 데이터1");
//        newProduct.setPrice(50.0);
//        newProduct.setCreatedDateTime("2024-02-01 11:11:11");
//        newProduct.setMemo("비고1");
//
//        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/producttest")
//                        .header(HttpHeaders.AUTHORIZATION, "ACCESS_TOKEN")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newProduct)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document("saveProduct",
//                        HeaderDocumentation.requestHeaders(
//                                HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("ACCESS_TOKEN")
//                        ),
//                        PayloadDocumentation.requestFields(
//                                PayloadDocumentation.fieldWithPath("id").description("상품번호"),
//                                PayloadDocumentation.fieldWithPath("vendorId").description("고객번호"),
//                                PayloadDocumentation.fieldWithPath("name").description("상품명"),
//                                PayloadDocumentation.fieldWithPath("price").description("금액"),
//                                PayloadDocumentation.fieldWithPath("createdDateTime").description("생성시기"),
//                                PayloadDocumentation.fieldWithPath("memo").description("비고")
//                        ),
//                        PayloadDocumentation.responseFields(
//                                PayloadDocumentation.fieldWithPath("id").description("상품번호"),
//                                PayloadDocumentation.fieldWithPath("vendorId").description("고객번호"),
//                                PayloadDocumentation.fieldWithPath("name").description("상품명"),
//                                PayloadDocumentation.fieldWithPath("price").description("금액"),
//                                PayloadDocumentation.fieldWithPath("contractCount").description("계약수"),
//                                PayloadDocumentation.fieldWithPath("createdDateTime").description("생성시기"),
//                                PayloadDocumentation.fieldWithPath("updatedDateTime").description("수정시기"),
//                                PayloadDocumentation.fieldWithPath("deletedDate").description("삭제시기"),
//                                PayloadDocumentation.fieldWithPath("memo").description("비고"),
//                                PayloadDocumentation.fieldWithPath("status").description("상태")
//                        )
//                ));
//
//    }
//
//    // 상품 상세 조회
//    @Test
//    void findProductById() throws Exception {
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/producttest/{PRODUCT_ID}", 1L)
//                        .header(HttpHeaders.AUTHORIZATION, "ACCESS_TOKEN")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document("findProductById",
//                        HeaderDocumentation.requestHeaders(
//                                HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("ACCESS_TOKEN")
//                        ),
//                        PayloadDocumentation.responseFields(
//                                PayloadDocumentation.fieldWithPath("id").description("상품번호"),
//                                PayloadDocumentation.fieldWithPath("vendorId").description("고객번호"),
//                                PayloadDocumentation.fieldWithPath("name").description("상품명"),
//                                PayloadDocumentation.fieldWithPath("price").description("금액"),
//                                PayloadDocumentation.fieldWithPath("contractCount").description("계약수"),
//                                PayloadDocumentation.fieldWithPath("createdDateTime").description("생성시기"),
//                                PayloadDocumentation.fieldWithPath("updatedDateTime").description("수정시기"),
//                                PayloadDocumentation.fieldWithPath("deletedDate").description("삭제시기"),
//                                PayloadDocumentation.fieldWithPath("memo").description("비고"),
//                                PayloadDocumentation.fieldWithPath("status").description("상태")
//                        )
//                ));
//    }
//
//    // 상품 수정
//    @Test
//    void updateProduct() throws Exception {
//
//        // 수정할 상품 정보 주입
//        TProductUpdateReq updateProduct = new TProductUpdateReq();
//        updateProduct.setName("상품 조회용 데이터2");
//        updateProduct.setPrice(20.0);
//        updateProduct.setMemo("비고 2");
//
//        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/vendor/producttest/{PRODUCT_ID}", 2L)
//                        .header(HttpHeaders.AUTHORIZATION, "ACCESS_TOKEN")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateProduct)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document("updateProduct",
//                        HeaderDocumentation.requestHeaders(
//                                HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("ACCESS_TOKEN")
//                        ),
//                        PayloadDocumentation.requestFields(
//                                PayloadDocumentation.fieldWithPath("name").description("상품명"),
//                                PayloadDocumentation.fieldWithPath("price").description("금액"),
//                                PayloadDocumentation.fieldWithPath("memo").description("비고")
//                        ),
//                        PayloadDocumentation.responseFields(
//                                PayloadDocumentation.fieldWithPath("id").description("상품번호"),
//                                PayloadDocumentation.fieldWithPath("vendorId").description("고객번호"),
//                                PayloadDocumentation.fieldWithPath("name").description("상품명"),
//                                PayloadDocumentation.fieldWithPath("price").description("금액"),
//                                PayloadDocumentation.fieldWithPath("contractCount").description("계약수"),
//                                PayloadDocumentation.fieldWithPath("createdDateTime").description("생성시기"),
//                                PayloadDocumentation.fieldWithPath("updatedDateTime").description("수정시기"),
//                                PayloadDocumentation.fieldWithPath("deletedDate").description("삭제시기"),
//                                PayloadDocumentation.fieldWithPath("memo").description("비고"),
//                                PayloadDocumentation.fieldWithPath("status").description("상태")
//                        )
//                ));
//    }
//
//    @Test
//    void deleteProduct() throws Exception {
//        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/producttest/{PRODUCT_ID}", 2L)
//                        .header(HttpHeaders.AUTHORIZATION, "ACCESS_TOKEN")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document("deleteProduct",
//                        HeaderDocumentation.requestHeaders(
//                                HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("ACCESS_TOKEN")
//                        ),
//                        PayloadDocumentation.responseFields(
//                                PayloadDocumentation.fieldWithPath("id").description("상품번호"),
//                                PayloadDocumentation.fieldWithPath("vendorId").description("고객번호"),
//                                PayloadDocumentation.fieldWithPath("name").description("상품명"),
//                                PayloadDocumentation.fieldWithPath("price").description("금액"),
//                                PayloadDocumentation.fieldWithPath("contractCount").description("계약수"),
//                                PayloadDocumentation.fieldWithPath("createdDateTime").description("생성시기"),
//                                PayloadDocumentation.fieldWithPath("updatedDateTime").description("수정시기"),
//                                PayloadDocumentation.fieldWithPath("deletedDate").description("삭제시기"),
//                                PayloadDocumentation.fieldWithPath("memo").description("비고"),
//                                PayloadDocumentation.fieldWithPath("status").description("상태")
//                        )
//                ));
//    }
//
//
//
//}
