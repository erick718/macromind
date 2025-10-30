package com.fitapp.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fitapp.dao.MilestoneDao;
import com.fitapp.model.Milestone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@WebServlet(name = "MilestonesServlet", urlPatterns = {"/api/milestones"})
public class MilestonesServlet extends HttpServlet {

    private final MilestoneDao dao = new MilestoneDao();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = (String) Optional.ofNullable(req.getSession(false))
                .map(HttpSession::getId).orElse("demo-user");

        LocalDate end = Optional.ofNullable(req.getParameter("end"))
                .map(LocalDate::parse).orElse(LocalDate.now());
        LocalDate start = Optional.ofNullable(req.getParameter("start"))
                .map(LocalDate::parse).orElse(end.minusDays(6));

        List<Milestone> list = dao.getMilestones(userId, start, end);

        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), Map.of(
                "start", start.toString(),
                "end", end.toString(),
                "milestones", list
        ));
    }
}
