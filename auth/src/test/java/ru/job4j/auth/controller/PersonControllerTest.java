package ru.job4j.auth.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {

    @MockBean
    private PersonRepository personRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenGetAllThenOk() throws Exception {
        this.mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(personRepository).findAll();

    }


    @Test
    public void whenFindPersonThenReturnPersonAndOk() throws Exception {
        Person person = new Person();
        person.setId(1);
        when(personRepository.findById(anyInt())).thenReturn(Optional.of(person));
        mockMvc.perform( MockMvcRequestBuilders
                .get("/person/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));

    }

    @Test
    public void whenCreateThenReturnPerson() throws Exception {
        Person person = Person.of("job", "password");
        person.setId(1);
        when(personRepository.save(any())).thenReturn(person);
        JSONObject jo = new JSONObject();
        jo.put("id", person.getId());
        jo.put("login", person.getLogin());
        jo.put("password", person.getPassword());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/person/")
                .content(String.valueOf(jo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void whenPutThenReturnOk() throws Exception {
        Person person = Person.of("job", "password");
        person.setId(1);
        JSONObject jo = new JSONObject();
        jo.put("id", person.getId());
        jo.put("login", person.getLogin());
        jo.put("password", person.getPassword());
        mockMvc.perform(MockMvcRequestBuilders
                .put("/person/")
                .content(String.valueOf(jo))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argumentCaptor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId(), is(1));
    }

    @Test
    public void whenDeleteThenReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/person/1"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argumentCaptor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository).delete(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId(), is(1));
    }

}