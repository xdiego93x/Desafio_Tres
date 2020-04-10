package com.desafio.previred.desafiotres.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.previred.desafio.tres.uf.Valores;
import com.previred.desafio.tres.uf.tools.RandomDate;
import com.previred.desafio.tres.uf.vo.Uf;
import com.previred.desafio.tres.uf.vo.Ufs;

@Service
public class DesafioServices {

	private static final Logger LOG = LogManager.getLogger(DesafioServices.class);

	@Autowired
	private ObjectMapper objectMapper;

	public String generateUfs() throws JsonProcessingException {
		Valores valores = new Valores();
		Ufs ufs = valores.getRango();
		Set<Uf> ufSet = ufs.getUfs();
		List<Uf> ufList;

		ufList = add(ufSet, ufs);

		Collections.sort(ufList, new Comparator<Uf>() {
			@Override
			public int compare(Uf ufFirst, Uf ufSecond) {
				return ufSecond.getFecha().compareTo(ufFirst.getFecha());
			}
		});
		ufs.setUfs(new LinkedHashSet<Uf>(ufList));
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ufs);
	}

	private List<Uf> add(Set<Uf> ufSet, Ufs ufs) {
		Calendar inicio = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
		inicio.setTime(ufs.getInicio());
		Calendar fin = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
		fin.setTime(ufs.getFin());
		RandomDate randomDate = new RandomDate(fechaParse(inicio), fechaParse(fin));
		Uf uf = new Uf();
		uf.setFecha(Date.from(randomDate.nextDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		double valorUF = (new Random()).nextDouble();
		uf.setValor(valorUF);
		ufSet.add(uf);
		LOG.info("Descarga de valores.json exitosa.");
		LOG.info("Desde {}",fechaParse(inicio));
		LOG.info("Hasta {}",fechaParse(fin));
		return new ArrayList<>(ufSet);
	}

	private LocalDate fechaParse(Calendar fecha) {		
		int year = fecha.get(Calendar.YEAR);
		int month = fecha.get(Calendar.MONTH) != 0 ? fecha.get(Calendar.MONTH) : 1; 
		int day = fecha.get(Calendar.DAY_OF_MONTH) != 31 ? fecha.get(Calendar.DAY_OF_MONTH) : 30;  
        return LocalDate.of(year, month, day);
    }

}