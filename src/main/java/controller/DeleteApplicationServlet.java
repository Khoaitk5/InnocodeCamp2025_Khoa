// File: src/main/java/controller/DeleteApplicationServlet.java
package controller;

import dao.JobApplicationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User;

@WebServlet(name = "DeleteApplicationServlet", urlPatterns = {"/DeleteApplicationServlet"})
public class DeleteApplicationServlet extends HttpServlet {
    private JobApplicationDAO applicationDAO;
    
    @Override
    public void init() throws ServletException {
        applicationDAO = new JobApplicationDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String jobIdStr = request.getParameter("jobId");
        
        if (jobIdStr == null || jobIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }
        
        try {
            int jobId = Integer.parseInt(jobIdStr);
            int userId = user.getId();
            
            // Kiểm tra xem ứng tuyển có tồn tại và thuộc về user này không
            boolean success = applicationDAO.deleteApplication(userId, jobId);
            
            if (success) {
                response.getWriter().write("success");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found or cannot be deleted");
            }
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Job ID format");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
} 