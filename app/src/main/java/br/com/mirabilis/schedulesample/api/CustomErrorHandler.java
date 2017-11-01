package br.com.mirabilis.schedulesample.api;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by ticketservices on 7/6/17.
 */

public class CustomErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {

        String errorDescription;

        if (cause.getKind() == RetrofitError.Kind.NETWORK)
            errorDescription = "Sem conexão com a Internet. Verifique sua conexão e tente novamente.";
        else {

            String msgServicoIndisponivel = "Serviço indisponível no momento, tente novamente mais tarde.";

            if (cause.getResponse() == null)
                errorDescription = msgServicoIndisponivel;
            else {
                try {

                    try {

                        errorDescription = cause.getBodyAs(ErrorBody.class) == null ?
                                msgServicoIndisponivel :
                                getError((ErrorBody) cause.getBodyAs(ErrorBody.class), msgServicoIndisponivel);

                    } catch (Exception e) {

                        errorDescription = cause.getBodyAs(String.class) == null ?
                                msgServicoIndisponivel : (String) cause.getBodyAs(String.class);

                    }

                } catch (Exception ex) {

                    try {
                        errorDescription = msgServicoIndisponivel;
                    } catch (Exception ex2) {
                        Log.e("CUSTOMERROR", "handleError: " + ex2.getLocalizedMessage());
                        errorDescription = msgServicoIndisponivel;
                    }

                }

            }

        }

        return new Exception(errorDescription);

    }

    private String getError(ErrorBody errorBody, String defaultErrorMessage) {
        if (errorBody.erro != null) {
            return errorBody.erro;
        }
        if (errorBody.message != null) {
            return errorBody.message;
        }

        return defaultErrorMessage;
    }

    class ErrorBody {

        public String erro;
        public String detalhes;
        public String message;

    }
}
