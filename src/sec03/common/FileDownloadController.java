package sec03.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileDownloadController
 */
@SuppressWarnings("serial")
@WebServlet("/download.do")
public class FileDownloadController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		String imageFileName = (String) request.getParameter("imageFileName");
		String articleNO = request.getParameter("articleNO");
		System.out.println("imageFileName=" + imageFileName);
		
		OutputStream out = response.getOutputStream();
		
		// DB직접 저장이아닌 스토리지형
		String path = ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + imageFileName;
		
		File imageFile = new File(path);

		// 파일 다운로드
		response.setHeader("Cache-Control", "no-cache");
		response.addHeader("Content-disposition", "attachment;fileName=" + imageFileName);
		
		FileInputStream in = new FileInputStream(imageFile);
		
		byte[] buffer = new byte[1024 * 8];	// 버퍼크기를 정한다 , 8kb 씩 
		while (true) {
			int count = in.read(buffer);	// 8바이트씩 읽는다 
			if (count == -1)				// 읽을게 없으면 -1
				break;						// 멈춰-!
			out.write(buffer, 0, count);	// 출력 스트림에서 buffer를 0부터 count까지 출력
		}									//
		in.close();							// 입력스트림 해제
		out.close();						// 출력스트림 해제
	}

}