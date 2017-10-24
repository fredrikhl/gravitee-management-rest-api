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
package io.gravitee.management.service.impl;

import io.gravitee.common.data.domain.Page;
import io.gravitee.common.utils.UUID;
import io.gravitee.management.model.*;
import io.gravitee.management.service.RatingService;
import io.gravitee.management.service.exceptions.RatingNotFoundException;
import io.gravitee.management.service.exceptions.TechnicalManagementException;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.RatingRepository;
import io.gravitee.repository.management.api.search.Pageable;
import io.gravitee.repository.management.model.Rating;
import io.gravitee.repository.management.model.RatingAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Azize ELAMRANI (azize at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class RatingServiceImpl extends AbstractService implements RatingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingServiceImpl.class);

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public RatingEntity create(final NewRatingEntity ratingEntity) {
        try {
            return convert(ratingRepository.create(convert(ratingEntity)));
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurred while trying to create rating on api {}", ratingEntity.getApi(), ex);
            throw new TechnicalManagementException("An error occurred while trying to create rating on api " + ratingEntity.getApi(), ex);
        }
    }

    @Override
    public RatingEntity createAnswer(final NewRatingAnswerEntity answerEntity) {
        try {
            final Rating rating = findById(answerEntity.getRatingId());

            final RatingAnswer ratingAnswer = new RatingAnswer();
            ratingAnswer.setUser(getAuthenticatedUsername());
            ratingAnswer.setComment(answerEntity.getComment());
            ratingAnswer.setCreatedAt(new Date());

            if (rating.getAnswers() == null) {
                rating.setAnswers(new ArrayList<>(1));
            }

            rating.getAnswers().add(ratingAnswer);
            return convert(ratingRepository.update(rating));
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurred while trying to create a rating answer on rating {}", answerEntity.getRatingId(), ex);
            throw new TechnicalManagementException("An error occurred while trying to create a rating answer on rating" + answerEntity.getRatingId(), ex);
        }
    }

    @Override
    public Page<RatingEntity> findByApi(final String api, final Pageable pageable) {
        try {
            final Page<Rating> pageRating = ratingRepository.findByApi(api, pageable);
            final List<RatingEntity> ratingEntities =
                    pageRating.getContent().stream().map(this::convert).collect(toList());
            return new Page<>(ratingEntities, pageRating.getPageNumber(),
                    (int) pageRating.getPageElements(), pageRating.getTotalElements());
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurred while trying to find ratings for api {}", api, ex);
            throw new TechnicalManagementException("An error occurred while trying to find ratings for api " + api, ex);
        }
    }

    @Override
    public RatingEntity update(final UpdateRatingEntity ratingEntity) {
        try {
            final Rating rating = findById(ratingEntity.getId());
            if (!rating.getApi().equals(ratingEntity.getApi())) {
                throw new RatingNotFoundException(ratingEntity.getId(), ratingEntity.getApi());
            }

            rating.setRate(ratingEntity.getRate());
            final Date now = new Date();
            rating.setUpdatedAt(now);
            return convert(ratingRepository.update(rating));
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurred while trying to update rating {}", ratingEntity.getId(), ex);
            throw new TechnicalManagementException("An error occurred while trying to update rating " + ratingEntity.getId(), ex);
        }
    }

    @Override
    public void delete(final String id) {
        try {
            findById(id);
            ratingRepository.delete(id);
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurs while trying to delete rating {}", id, ex);
            throw new TechnicalManagementException("An error occurs while trying to delete rating " + id, ex);
        }
    }

    private Rating findById(String id) {
        try {
            final Optional<Rating> ratingOptional = ratingRepository.findById(id);
            if (!ratingOptional.isPresent()) {
                throw new RatingNotFoundException(id);
            }
            return ratingOptional.get();
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurred while trying to find a rating by id {}", id, ex);
            throw new TechnicalManagementException("An error occurred while trying to find a rating by id " + id, ex);
        }
    }

    private RatingEntity convert(final Rating rating) {
        final RatingEntity ratingEntity = new RatingEntity();
        ratingEntity.setId(rating.getId());
        ratingEntity.setApi(rating.getApi());
        ratingEntity.setUser(rating.getUser());
        ratingEntity.setTitle(rating.getTitle());
        ratingEntity.setComment(rating.getComment());
        ratingEntity.setRate(rating.getRate());
        ratingEntity.setCreatedAt(rating.getCreatedAt());
        ratingEntity.setUpdatedAt(rating.getUpdatedAt());
        if (rating.getAnswers() != null) {
            ratingEntity.setAnswers(rating.getAnswers().stream().map(ratingAnswer -> {
                final RatingAnswerEntity ratingAnswerEntity = new RatingAnswerEntity();
                ratingAnswerEntity.setUser(ratingAnswer.getUser());
                ratingAnswerEntity.setComment(ratingAnswer.getComment());
                ratingAnswerEntity.setCreatedAt(ratingAnswer.getCreatedAt());
                return ratingAnswerEntity;
            }).collect(toList()));
        }
        return ratingEntity;
    }

    private Rating convert(final NewRatingEntity ratingEntity) {
        final Rating rating = new Rating();
        rating.setId(UUID.toString(UUID.random()));
        rating.setApi(ratingEntity.getApi());
        rating.setRate(ratingEntity.getRate());
        rating.setTitle(ratingEntity.getTitle());
        rating.setComment(ratingEntity.getComment());
        rating.setUser(getAuthenticatedUsername());
        final Date now = new Date();
        rating.setCreatedAt(now);
        rating.setUpdatedAt(now);
        return rating;
    }
}
