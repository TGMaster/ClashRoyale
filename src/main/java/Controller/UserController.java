/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.Player;
import Service.PlayerService;
import Util.APIStatus;
import Util.ResponseUtil;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

/**
 *
 * @author TGMaster
 */
public class UserController extends HttpServlet {

    protected ResponseUtil responseUtil;
    protected Gson gson = new Gson();
    private PlayerService playerService = new PlayerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Call Servlet Context
        ServletContext sc = getServletContext();

        // Declare requestDispatcher
        RequestDispatcher rd;

        String action = request.getParameter("action");

        if (action == null) {
            rd = sc.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
        } else if (action.equals("login")) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String login = Login(username, password, request);
            response.getWriter().write(login);
            rd = sc.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
        } else if (action.equals("logout")) {
            request.getSession().removeAttribute("player");
            response.sendRedirect("user");
        }
    }

    private String Login(String username, String password, HttpServletRequest request) throws IOException {

        if (username.equals("") || password.equals("")) {
            responseUtil = new ResponseUtil(APIStatus.ERR_BAD_PARAMS);
        } else {
            // Read database

            boolean isLogin = false;
            Player p = playerService.findPlayerByUsername(username);
            if (p != null) {
                if (p.getPassword().equals(password)) {
                    isLogin = true;

                    // Call session
                    HttpSession session = request.getSession();
                    session.setAttribute("player", p);
                }
            } else {
                responseUtil = new ResponseUtil(APIStatus.ERR_USER_NOT_FOUND);
            }

            if (isLogin) {
                responseUtil = new ResponseUtil(APIStatus.OK);
            } else {
                responseUtil = new ResponseUtil(APIStatus.ERR_PASSWORD_NOT_MATCH);
            }
        }

        return gson.toJson(responseUtil);
    }
}
