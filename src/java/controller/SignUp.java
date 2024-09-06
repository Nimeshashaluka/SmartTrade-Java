package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Response_DTO;
import dto.User_DTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import entity.User;
import model.HibernateUtil;
import model.Mail;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        User_DTO user_DTO = gson.fromJson(request.getReader(), User_DTO.class);

        if (user_DTO.getFirst_name().isEmpty()) {
            response_DTO.setContent("Please enter your first Name");

        } else if (user_DTO.getLast_name().isEmpty()) {
            response_DTO.setContent("Please enter your last Name");

        } else if (user_DTO.getEmail().isEmpty()) {
            response_DTO.setContent("Please enter your Email");

        } else if (!Validation.isEmailValid(user_DTO.getEmail())) {
            response_DTO.setContent("Please enter a valid Email");

        } else if (user_DTO.getPassword().isEmpty()) {
            response_DTO.setContent("Please enter your Password");

        } else if (!Validation.isPasswordValid(user_DTO.getPassword())) {
            response_DTO.setContent("Password must include at least one"
                    + " uppercase lette, number, special character");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));

            if (!criteria1.list().isEmpty()) {
                response_DTO.setContent("User with this email Already Exists");
            } else {
                //generate varification code
                int code = (int) (Math.random() * 1000000);

                final User user = new User();
                user.setEmail(user_DTO.getEmail());
                user.setFirst_name(user_DTO.getFirst_name());
                user.setLast_name(user_DTO.getLast_name());
                user.setPassword(user_DTO.getPassword());
                user.setVerification(String.valueOf(code));

                //
                //send verification code
                Thread sendMailThread = new Thread() {
                    @Override
                    public void run() {
                        Mail.sendMail(user.getEmail(), "Smart Trade Verifiactiopn",
                                "<h1 style=\"color:#6482AD;\">Your Verifiacation Code : " + user.getVerification() + "</h1>"
                        );
                    }

                };
                sendMailThread.start();

                session.save(user);
                session.beginTransaction().commit();
                request.getSession().setAttribute("email", user_DTO.getEmail());

                request.getSession().setAttribute("email", user_DTO.getEmail());
                response_DTO.setSuccess(true);
                response_DTO.setContent("Registration success.Please Verify Your Verification code in mail Box");
            }
            session.close();
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));
    }

}
