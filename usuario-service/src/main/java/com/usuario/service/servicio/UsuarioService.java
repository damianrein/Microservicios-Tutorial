package com.usuario.service.servicio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.usuario.service.entity.Usuario;
import com.usuario.service.feignClients.CarroFeignClient;
import com.usuario.service.feignClients.MotoFeignClient;
import com.usuario.service.modelos.Carro;
import com.usuario.service.modelos.Moto;
import com.usuario.service.repository.UsuarioRepositorio;

@Service
public class UsuarioService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private UsuarioRepositorio usuarioRepository;
	
	@Autowired
	private CarroFeignClient carroFeignClient;
	
	@Autowired
	private MotoFeignClient motoFeignClient;
	
	public List<Usuario> getAll(){
		return usuarioRepository.findAll();
	}
	
	public Usuario getUsuarioById(Integer id) {
		return usuarioRepository.findById(id).orElse(null);
	}
	
	public Usuario save(Usuario usuario) {
		 Usuario nuevoUsuario=usuarioRepository.save(usuario);
		 return nuevoUsuario;
	}
	
	public List<Carro> getCarros(int usuarioId){
		List<Carro> carros=restTemplate.getForObject("http://localhost:8082/carro/usuario/" + usuarioId, List.class);
		return carros;
	}
	
	public List<Moto> getMotos(int usuarioId){
		List<Moto> motos=restTemplate.getForObject("http://localhost:8083/moto/usuario/" + usuarioId, List.class);
		return motos;}
	
	public Carro saveCarro (int usuarioId, Carro carro) {
		carro.setUsuarioId(usuarioId);
		Carro nuevoCarro=carroFeignClient.save(carro);
		return nuevoCarro;
	}
	
	public Moto saveMoto (int usuarioId, Moto moto) {
		moto.setUsuarioId(usuarioId);
		Moto nuevaMoto=motoFeignClient.save(moto);
		return nuevaMoto;
	}
	
	public Map<String, Object> getUsuarioAndVehiculos(int usuarioId){
		Map<String, Object> resultado=new HashMap<>();
		Usuario usuario=usuarioRepository.findById(usuarioId).orElse(null);
		
		if(resultado==null) {
			resultado.put("Resultado", "El usuario no existe");
			return resultado;}
		
		resultado.put("Usuario", usuario);
		List<Carro> carros=carroFeignClient.getCarros(usuarioId);
		if(carros.isEmpty()) {
			resultado.put("Carro", "El usuario no tiene carros");
		}else {
			resultado.put("Carro", carros);}
		
		List<Moto> motos=motoFeignClient.getMotos(usuarioId);
		if(motos.isEmpty()) {
			resultado.put("Moto", "El usuario no tiene motos");
		}else {
			resultado.put("Moto", motos);}
		
		return resultado;
	}
}
