/*
 * Copyright (c) 2015 Alexandre Almeida.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aalmeida.citizencard;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class PhotoServlet.
 *
 * @author Alexandre
 */
public class PhotoServlet extends HttpServlet {

    private static final long serialVersionUID = 5496948867239727953L;
    private static final String PIC_PATH = "pictures";

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, ImagingOpException, IOException {
        final String nif = request.getParameter("nif");
        final String photoPath = PIC_PATH + File.separatorChar + nif + ".bmp";
        File file = new File(photoPath);
        if (!file.exists()) {
            return;
        }
        BufferedImage bi = ImageIO.read(file);
        OutputStream out = response.getOutputStream();
        ImageIO.write(bi, "bmp", out);
        out.close();
    }

    @Override
    public void destroy() {
    }
}
