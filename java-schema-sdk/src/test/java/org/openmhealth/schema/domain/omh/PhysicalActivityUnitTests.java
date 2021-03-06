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
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmhealth.schema.domain.omh.DescriptiveStatistic.MINIMUM;
import static org.openmhealth.schema.domain.omh.DurationUnit.DAY;
import static org.openmhealth.schema.domain.omh.LengthUnit.KILOMETER;
import static org.openmhealth.schema.domain.omh.LengthUnit.MILE;
import static org.openmhealth.schema.domain.omh.PartOfDay.MORNING;
import static org.openmhealth.schema.domain.omh.PhysicalActivity.SelfReportedIntensity.LIGHT;
import static org.openmhealth.schema.domain.omh.PhysicalActivity.SelfReportedIntensity.MODERATE;


/**
 * @author Emerson Farrugia
 */
public class PhysicalActivityUnitTests extends SerializationUnitTests {

    public static final String SCHEMA_FILENAME = "schema/omh/physical-activity-1.0.json";

    @Test(expectedExceptions = NullPointerException.class)
    public void constructorShouldThrowExceptionOnUndefinedActivityName() {

        new PhysicalActivity.Builder(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionOnEmptyActivityName() {

        new PhysicalActivity.Builder("");
    }

    @Test
    public void buildShouldConstructMeasureUsingOnlyRequiredProperties() {

        PhysicalActivity physicalActivity = new PhysicalActivity.Builder("walking").build();

        assertThat(physicalActivity, notNullValue());
        assertThat(physicalActivity.getActivityName(), equalTo("walking"));
        assertThat(physicalActivity.getDistance(), nullValue());
        assertThat(physicalActivity.getReportedActivityIntensity(), nullValue());
        assertThat(physicalActivity.getEffectiveTimeFrame(), nullValue());
        assertThat(physicalActivity.getDescriptiveStatistic(), nullValue());
        assertThat(physicalActivity.getUserNotes(), nullValue());
    }

    @Test
    public void buildShouldConstructMeasureUsingOptionalProperties() {

        LengthUnitValue distance = new LengthUnitValue(KILOMETER, ONE);

        TimeInterval effectiveTimeInterval = TimeInterval.ofEndDateTimeAndDuration(
                OffsetDateTime.now(),
                new DurationUnitValue(DAY, TEN));

        PhysicalActivity physicalActivity = new PhysicalActivity.Builder("walking")
                .setDistance(distance)
                .setReportedActivityIntensity(LIGHT)
                .setEffectiveTimeFrame(effectiveTimeInterval)
                .setDescriptiveStatistic(MINIMUM)
                .setUserNotes("feeling fine")
                .build();

        assertThat(physicalActivity, notNullValue());
        assertThat(physicalActivity.getActivityName(), equalTo("walking"));
        assertThat(physicalActivity.getDistance(), equalTo(distance));
        assertThat(physicalActivity.getReportedActivityIntensity(), equalTo(LIGHT));
        assertThat(physicalActivity.getEffectiveTimeFrame(), equalTo(new TimeFrame(effectiveTimeInterval)));
        assertThat(physicalActivity.getDescriptiveStatistic(), equalTo(MINIMUM));
        assertThat(physicalActivity.getUserNotes(), equalTo("feeling fine"));
    }

    @Override
    protected String getSchemaFilename() {
        return SCHEMA_FILENAME;
    }

    @Test
    public void measureShouldSerializeCorrectly() throws Exception {

        PhysicalActivity physicalActivity = new PhysicalActivity.Builder("walking")
                .setDistance(new LengthUnitValue(MILE, BigDecimal.valueOf(1.5)))
                .setReportedActivityIntensity(MODERATE)
                .setEffectiveTimeFrame(TimeInterval.ofDateAndPartOfDay(LocalDate.of(2013, 2, 5), MORNING))
                .build();

        String document = "{\n" +
                "    \"activity_name\": \"walking\",\n" +
                "    \"distance\": {\n" +
                "        \"value\": 1.5,\n" +
                "        \"unit\": \"mi\"\n" +
                "    },\n" +
                "    \"reported_activity_intensity\": \"moderate\",\n" +
                "    \"effective_time_frame\": {\n" +
                "        \"time_interval\": {\n" +
                "            \"date\": \"2013-02-05\",\n" +
                "            \"part_of_day\": \"morning\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        serializationShouldCreateValidDocument(physicalActivity, document);
        deserializationShouldCreateValidObject(document, physicalActivity);
    }
}