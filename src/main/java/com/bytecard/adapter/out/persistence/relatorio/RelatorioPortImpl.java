package com.bytecard.adapter.out.persistence.relatorio;

import com.bytecard.adapter.out.persistence.relatorio.entity.GastoCategoriaView;
import com.bytecard.adapter.out.persistence.relatorio.repository.GastoCategoriaViewRepository;
import com.bytecard.domain.model.GastoCategoria;
import com.bytecard.domain.port.out.transacao.RelatorioTransacaoPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RelatorioPortImpl implements RelatorioTransacaoPort{

    private final GastoCategoriaViewRepository gastoCategoriaViewRepository;

    public RelatorioPortImpl(GastoCategoriaViewRepository gastoCategoriaViewRepository) {
        this.gastoCategoriaViewRepository = gastoCategoriaViewRepository;
    }

    @Override
    public List<GastoCategoria> getSomatorioGastosPorCategoriaNoMes(Long id, Integer ano, Integer mes) {
        List<GastoCategoriaView> resultadoView = gastoCategoriaViewRepository.findByIdCartaoIdAndIdAnoAndIdMesOrderByIdCategoria(id, ano, mes);

        return resultadoView.stream()
                .map(view -> new GastoCategoria(
                        view.getId().getCategoria(),
                        view.getTotal()
                ))
                .collect(Collectors.toList());
    }

}