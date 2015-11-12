package com.salesoptimizer.oauth.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.salesoptimizer.oauth.AuthConstants;
import com.salesoptimizer.oauth.AuthorizeManager;
import com.salesoptimizer.oauth.CommonUtils;

public class AuthorizationsListServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
	
    private static final String ACTION_TYPE_DELETE = "DELETE";
    private static final String PARAM_DELETE_TYPE = "deleteType";
    private static final String DELETE_TYPE_SINGLE = "SINGLE";
    private static final String DELETE_TYPE_ALL = "ALL";
    private static final String PARAM_ITEM_ID = "itemId";
    
    private static final String ATTR_AVAILABLE_ITEMS = "availableItems";
    
    private static final String ERROR_MSG_DELETE_SINGLE_NOT_IMPLEMENTED = "Deleting single item not implemented yet.";
    private static final String ERROR_MSG_DELETE_ALL_NOT_IMPLEMENTED = "Deleting all items not implemented yet.";



    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// process list
	    request.setAttribute(ATTR_AVAILABLE_ITEMS, buildAvailableItems(AuthorizeManager.getInstance()));
	    request.getRequestDispatcher("/authList.jsp").forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String actionType = request.getParameter(AuthConstants.PARAM_ACTION_TYPE);
	    if (ACTION_TYPE_DELETE.equalsIgnoreCase(actionType)) {
	        doDelete(request, response);
	    } else {
	        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            request.setAttribute(AuthConstants.PARAM_ERROR_MSG, "Cannot process ActionType: " + actionType);
            request.getRequestDispatcher("/authList.jsp").forward(request, response);
            return;
	    }
	}

	private static List<String> buildAvailableItems(AuthorizeManager instance) {
	    return instance != null ? instance.listTokenKeys() : new ArrayList<String>();
    }

    @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DeleteType deleteType = getDeleteType(request);
        if (DeleteType.ALL == deleteType) {
            doDeleteAll(request, response);
        } else if (DeleteType.SINGLE == deleteType) {
            doDeleteSingleItem(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute(AuthConstants.PARAM_ERROR_MSG, "No delete type provided.");
        }
        request.getRequestDispatcher("/authList.jsp").forward(request, response);
	}
    
    private enum DeleteType {
        SINGLE,
        ALL
    }
    
    private DeleteType getDeleteType(HttpServletRequest request) {
        DeleteType result = null;
        String deleteType = request.getParameter(PARAM_DELETE_TYPE);
        if (DELETE_TYPE_ALL.equalsIgnoreCase(deleteType)) {
            result = DeleteType.ALL;
        } else if (DELETE_TYPE_SINGLE.equalsIgnoreCase(deleteType)) {
            result = DeleteType.SINGLE;
        }
        return result;
    }
    
    private void doDeleteAll(HttpServletRequest request, HttpServletResponse response) {
        if (AuthorizeManager.getInstance().deleteAllTokens()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute(AuthConstants.PARAM_ERROR_MSG, "Failed to delete all tokens.");
        }
        
    }

    private void doDeleteSingleItem(HttpServletRequest request, HttpServletResponse response) {
        String itemId = request.getParameter(PARAM_ITEM_ID);
        if (CommonUtils.isNotBlank(itemId) && AuthorizeManager.getInstance().deleteToken(itemId)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute(AuthConstants.PARAM_ERROR_MSG, "Failed to delete token: '" + itemId + "'");
        }
    }
}
