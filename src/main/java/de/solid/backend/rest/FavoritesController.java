package de.solid.backend.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.solid.backend.dao.FavoriteEntity;
import de.solid.backend.dao.HelperEntity;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.FavoritesRepository;
import de.solid.backend.dao.repository.HelpersRepository;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.model.FavoriteRequestModel;
import de.solid.backend.rest.model.FavoriteResponseModel;
import io.quarkus.security.Authenticated;

@OpenAPIDefinition(tags = {
		@Tag(name = "widget", description = "Widget operations."),
		@Tag(name = "gasket", description = "Operations related to gaskets")}, info = @Info(title = "Example API", version = "1.0.1", contact = @Contact(name = "Example API Support", url = "http://exampleurl.com/contact", email = "techsupport@example.com"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
@Path("/favorites")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class FavoritesController extends BaseController {

	@Inject
	private HelpersRepository helpersRepository;

	@Inject
	private FavoritesRepository favoritesRepository;

	@Inject
	private ProvidersRepository providersRepository;

	@Inject
	@Claim("email")
	private String email;

	@Operation(description = "get all favorites for given helper")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFavorites() {
		HelperEntity helperEntity = this.helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			List<FavoriteEntity> entities = this.favoritesRepository
					.findByHelperId(helperEntity.getT_id());
			return HTTP_OK(entities.stream().map(
					entity -> new FavoriteResponseModel().fromEntity(entity))
					.collect(Collectors.toList()));
		}
		return NOT_FOUND();
	}

	@Operation(description = "saves the given provider as favorite for the given helper")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response markFavorite(
			@RequestBody FavoriteRequestModel favoriteRequestModel) {
		HelperEntity helperEntity = this.helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			ProviderEntity providerEntity = this.providersRepository
					.findById(favoriteRequestModel.getProviderId());
			if (providerEntity != null) {
				FavoriteEntity fe = this.favoritesRepository
						.findByHelperAndProvider(helperEntity.getT_id(),
								favoriteRequestModel.getProviderId());
				if (fe == null) {
					fe = FavoriteEntity.builder().build();
					fe.setHelper(helperEntity);
					fe.setProvider(providerEntity);
					this.favoritesRepository.persist(fe);
					return HTTP_CREATED();
				} else {
					return HTTP_CONFLICT("Provider already marked as favorite");
				}
			}
		}
		return NOT_FOUND();

	}

	@DELETE
	@Path("/{favoriteid})")
	@Operation(description = "remove a favorite with given favoriteId")
	@Transactional
	public Response deleteFavorite(
			@Parameter(description = "id of the favorite dataset") @PathParam("favoriteid") long favoriteId) {
		HelperEntity helperEntity = this.helpersRepository.findByEmail(email);
		if (helperEntity != null) {
			this.favoritesRepository.deleteById(favoriteId);
			return HTTP_OK();
		}
		return NOT_FOUND();
	}

}