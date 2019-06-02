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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author TGMaster
 */
public class UserController extends HttpServlet {

        protected ResponseUtil responseUtil;
        protected Gson gson = new Gson();
        private PlayerService playerService;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {

        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {

                // Call Servlet Context
                ServletContext sc = getServletContext();

                // Declare requestDispatcher
                RequestDispatcher rd;

                // Call session
                HttpSession session = request.getSession();

                String action = request.getParameter("action");

                if (action == null) {
                        rd = sc.getRequestDispatcher("/login.jsp");
                        rd.forward(request, response);
                } else {
                        String username = request.getParameter("username");
                        String password = request.getParameter("password");
                        String login = Login(username, password);
                        response.getWriter().write(login);
                }
        }

        private String Login(String username, String password) throws IOException {

                if (username.equals("") || password.equals("")) {
                        responseUtil = new ResponseUtil(APIStatus.ERR_BAD_PARAMS);
                } else {
                        // Read database

                        boolean isLogin = false;
                        Player p = playerService.findPlayerByUsername(username);
                        if (p != null) {
                            if (p.getPassword().equals(password)) {
                                isLogin = true;
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
