package decola.tech.board.persistence.dao;

import java.sql.Connection;

import decola.tech.board.dto.CardDetails;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardDAO {
    
    private final Connection connection;
    
    private CardDetails findById(final Long id) {
        return null;
    }

}
