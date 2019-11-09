package com.javatechie.spring.docs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(SpringRestDocsApplication.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class SpringRestDocsApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }



    @Test
    public void testShouldAddBook() throws Exception {
        Book book = new Book(199, "Hibernate", 800);
        String bookJson = new ObjectMapper().writeValueAsString(book);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/books")
                .content(bookJson)
                .contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(bookJson))
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").description("Book Identifier"),
                                fieldWithPath("name").description("Book Name"),
                                fieldWithPath("price").description("Book Price")
                        )))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBooks() throws Exception {
        String response = "[{\"id\":101,\"name\":\"core java\",\"price\":500.0},{\"id\":102,\"name\":\"spring boot\",\"price\":800.0}]";
        this.mockMvc
                .perform(RestDocumentationRequestBuilders.get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response))
                .andExpect(content().contentType("application/json"))
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("").optional().
                                description(response))));
    }
}
