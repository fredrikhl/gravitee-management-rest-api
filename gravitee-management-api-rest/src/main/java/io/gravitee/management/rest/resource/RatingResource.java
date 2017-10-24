/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.rest.resource;

import io.gravitee.common.data.domain.Page;
import io.gravitee.common.http.MediaType;
import io.gravitee.management.model.NewRatingAnswerEntity;
import io.gravitee.management.model.NewRatingEntity;
import io.gravitee.management.model.RatingEntity;
import io.gravitee.management.model.UpdateRatingEntity;
import io.gravitee.management.model.permissions.RolePermission;
import io.gravitee.management.model.permissions.RolePermissionAction;
import io.gravitee.management.rest.security.Permission;
import io.gravitee.management.rest.security.Permissions;
import io.gravitee.management.service.RatingService;
import io.gravitee.repository.management.api.search.builder.PageableBuilder;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;

/**
 * @author Azize ELAMRANI (azize at graviteesource.com)
 * @author GraviteeSource Team
 */
@Api(tags = {"Rating"})
public class RatingResource extends AbstractResource {

    @Autowired
    private RatingService ratingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_RATING, acls = RolePermissionAction.READ)
    })
    public Page<RatingEntity> list(@PathParam("api") String api, @QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
        return ratingService.findByApi(api, new PageableBuilder().pageNumber(pageNumber).pageSize(pageSize).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_RATING, acls = RolePermissionAction.CREATE)
    })
    public RatingEntity create(@Valid @NotNull final NewRatingEntity rating) {
        return ratingService.create(rating);
    }

    @Path("{rating}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_RATING_ANSWER, acls = RolePermissionAction.CREATE)
    })
    public RatingEntity createAnswer(@PathParam("rating") String rating, @Valid @NotNull final NewRatingAnswerEntity answer) {
        answer.setRatingId(rating);
        return ratingService.createAnswer(answer);
    }

    @Path("{rating}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_RATING, acls = RolePermissionAction.UPDATE)
    })
    public RatingEntity update(@PathParam("rating") String rating, @Valid @NotNull final UpdateRatingEntity ratingEntity) {
        ratingEntity.setId(rating);
        return ratingService.update(ratingEntity);
    }

    @Path("{rating}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_RATING, acls = RolePermissionAction.DELETE)
    })
    public void delete(@PathParam("rating") String rating) {
        ratingService.delete(rating);
    }
}
