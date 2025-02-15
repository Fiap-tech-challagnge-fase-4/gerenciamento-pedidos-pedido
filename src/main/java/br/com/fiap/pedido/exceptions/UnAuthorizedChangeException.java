package br.com.fiap.pedido.exceptions;

public class UnAuthorizedChangeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UnAuthorizedChangeException(String mensagem){
        super(mensagem);
    }
}
