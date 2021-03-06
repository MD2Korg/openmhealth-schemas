/*
 * Copyright 2015 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openmhealth.schema.domain.omh;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static java.math.BigDecimal.TEN;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmhealth.schema.domain.omh.DescriptiveStatistic.AVERAGE;
import static org.openmhealth.schema.domain.omh.DurationUnit.DAY;
import static org.openmhealth.schema.domain.omh.HeartRateUnit.BEATS_PER_MINUTE;
import static org.openmhealth.schema.domain.omh.TemporalRelationshipToPhysicalActivity.AT_REST;


/**
 * @author Emerson Farrugia
 */
public class HeartRateUnitTests extends SerializationUnitTests {

    public static final String SCHEMA_FILENAME = "schema/omh/heart-rate-1.0.json";

    @Test
    public void buildShouldConstructMeasureUsingOnlyRequiredProperties() {

        BigDecimal heartRateValue = BigDecimal.valueOf(60);

        HeartRate heartRate = new HeartRate.Builder(heartRateValue).build();

        assertThat(heartRate, notNullValue());
        assertThat(heartRate.getHeartRate(), equalTo(new TypedUnitValue<>(BEATS_PER_MINUTE, heartRateValue)));
        assertThat(heartRate.getEffectiveTimeFrame(), nullValue());
        assertThat(heartRate.getDescriptiveStatistic(), nullValue());
        assertThat(heartRate.getUserNotes(), nullValue());
    }

    @Test
    public void buildShouldConstructMeasureUsingOptionalProperties() {

        BigDecimal heartRateValue = BigDecimal.valueOf(60);

        TimeInterval effectiveTimeInterval = TimeInterval.ofEndDateTimeAndDuration(
                OffsetDateTime.now(),
                new DurationUnitValue(DAY, TEN));

        HeartRate heartRate = new HeartRate.Builder(heartRateValue)
                .setEffectiveTimeFrame(effectiveTimeInterval)
                .setDescriptiveStatistic(AVERAGE)
                .setUserNotes("feeling fine")
                .build();

        assertThat(heartRate, notNullValue());
        assertThat(heartRate.getHeartRate(), equalTo(new TypedUnitValue<>(BEATS_PER_MINUTE, heartRateValue)));
        assertThat(heartRate.getEffectiveTimeFrame(), equalTo(new TimeFrame(effectiveTimeInterval)));
        assertThat(heartRate.getDescriptiveStatistic(), equalTo(AVERAGE));
        assertThat(heartRate.getUserNotes(), equalTo("feeling fine"));
    }

    @Override
    protected String getSchemaFilename() {
        return SCHEMA_FILENAME;
    }

    @Test
    public void measureShouldSerializeCorrectly() throws Exception {

        HeartRate heartRate = new HeartRate.Builder(BigDecimal.valueOf(50))
                .setEffectiveTimeFrame(OffsetDateTime.of(2013, 2, 5, 7, 25, 0, 0, UTC))
                .setTemporalRelationshipToPhysicalActivity(AT_REST)
                .setUserNotes("I felt quite dizzy")
                .build();

        String document = "{\n" +
                "    \"heart_rate\": {\n" +
                "        \"value\": 50,\n" +
                "        \"unit\": \"beats/min\"\n" +
                "    },\n" +
                "    \"effective_time_frame\": {\n" +
                "        \"date_time\": \"2013-02-05T07:25:00Z\"\n" +
                "    },\n" +
                "    \"temporal_relationship_to_physical_activity\": \"at rest\",\n" +
                "    \"user_notes\": \"I felt quite dizzy\"\n" +
                "}";

        serializationShouldCreateValidDocument(heartRate, document);
        deserializationShouldCreateValidObject(document, heartRate);
    }
}