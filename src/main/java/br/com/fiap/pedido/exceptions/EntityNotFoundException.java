package br.com.fiap.pedido.exceptions;

public class EntityNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
    public EntityNotFoundException(Integer id){
        super("Pedido não encontrado com id: " + id);
    }

    public EntityNotFoundException(String mensagem){
        super(mensagem);
    }
}
