package com.fitapp.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fitapp.dao.ProgressDao;
import com.fitapp.model.ProgressPoint;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@WebServlet(name = "ProgressDataServlet", urlPatterns = {"/api/progress"})
public class ProgressDataServlet extends HttpServlet {

    private final ProgressDao dao = new ProgressDao();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // auth: for demo, use a fixed userId or session attribute
        String userId = (String) Optional.ofNullable(req.getSession(false))
                .map(HttpSession::getId).orElse("demo-user");

        // query params: range=7d|30d|90d or custom ?start=YYYY-MM-DD&end=YYYY-MM-DD
        String range = Optional.ofNullable(req.getParameter("range")).orElse("7d");
        LocalDate end = Optional.ofNullable(req.getParameter("end"))
                .map(LocalDate::parse).orElse(LocalDate.now());
        LocalDate start = Optional.ofNullable(req.getParameter("start"))
                .map(LocalDate::parse)
                .orElseGet(() -> end.minusDays(parseDays(range) - 1));

        // AC: must have at least 7 days
        if (start.isAfter(end) || start.plusDays(6).isAfter(end)) {
            resp.setStatus(400);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), Map.of(
                    "message", "Need at least 7 days of data",
                    "start", start.toString(), "end", end.toString()
            ));
            return;
        }

        List<ProgressPoint> points = dao.getProgress(userId, start, end);

        // Return as a unified payload for the three charts
        Map<String, Object> payload = new HashMap<>();
        payload.put("start", start.toString());
        payload.put("end", end.toString());
        payload.put("data", points);

        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), payload);
    }

    private int parseDays(String range) {
        try {
            if (range.endsWith("d")) {
                return Integer.parseInt(range.substring(0, range.length() - 1));
            }
        } catch (NumberFormatException ignored) {}
        return 7; // default
    }
}
