package com.deltasac.deltanet.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.deltasac.deltanet.models.entity.Persona;
import com.deltasac.deltanet.models.service.IPersonaService;

@Component
public class InfoAdicionalToken implements TokenEnhancer {
	
	@Autowired
	private IPersonaService personaService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		Persona persona = personaService.findByUsername(authentication.getName());
		Map<String, Object> info = new HashMap<>();
		info.put("info adicional", "Hola que tal ".concat(authentication.getName()));
		info.put("usuario_persona", persona.getId() + ": " + persona.getUsername());
		info.put("dni_persona", persona.getNrodoc());
		info.put("nombre_persona", persona.getNomper());
		info.put("email_persona", persona.getEmail());
		info.put("apellido_persona", persona.getApeper());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		return accessToken;
	}

}
