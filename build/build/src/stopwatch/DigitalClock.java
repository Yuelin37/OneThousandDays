/*
 * Copyright (c) 2008, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stopwatch;

import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DigitalClock extends Parent {

	private final VBox vBox = new VBox();
	private final HBox hourBox = new HBox();
	private final HBox hBox = new HBox();
	public final Font FONT = new Font(16);
	public final Font HOURFONT = new Font(26);
	private Text[] hours = new Text[2];
	private Text[] digits = new Text[8];
	private Group[] hoursGroup = new Group[2];
	private Group[] digitsGroup = new Group[8];
	private int[] hourNumbers = { 0, 1 };
	private int[] numbers = { 0, 1, 3, 4, 6, 7 };

	DigitalClock() {
		configureHours();
		configureDigits();
		configureDots();
		configureHourHbox();
		configureHbox();
		hourBox.setAlignment(Pos.CENTER);
		vBox.setAlignment(Pos.CENTER);
		vBox.setSpacing(3);
		vBox.getChildren().addAll(hourBox, hBox);
		getChildren().addAll(vBox);
	}

	private void configureHours() {
		for (int i : hourNumbers) {
			hours[i] = new Text("0");
			hours[i].setFont(HOURFONT);
			hours[i].setTextOrigin(VPos.TOP);
			hours[i].setLayoutX(2.3);
			hours[i].setLayoutY(-1);
			Rectangle background;

			background = createHourBackground(Color.web("#a39f91"), Color.web("#FFFFFF"));
			hours[i].setFill(Color.web("#000000"));
			hoursGroup[i] = new Group(background, hours[i]);
		}
	}

	private void configureDigits() {
		for (int i : numbers) {
			digits[i] = new Text("0");
			digits[i].setFont(FONT);
			digits[i].setTextOrigin(VPos.TOP);
			digits[i].setLayoutX(2.3);
			digits[i].setLayoutY(-1);
			Rectangle background;
			if (i < 6) {
				background = createBackground(Color.web("#a39f91"), Color.web("#FFFFFF"));
				digits[i].setFill(Color.web("#000000"));
			} else {
				background = createBackground(Color.web("#bdbeb3"), Color.web("#FF0000"));
				digits[i].setFill(Color.web("#FFFFFF"));
			}
			digitsGroup[i] = new Group(background, digits[i]);
		}
	}

	private void configureDots() {
		digits[2] = createDot(":");
		digitsGroup[2] = new Group(createDotBackground(), digits[2]);
		digits[5] = createDot(".");
		digitsGroup[5] = new Group(createDotBackground(), digits[5]);
	}

	private Rectangle createDotBackground() {
		Rectangle background = new Rectangle(8, 17, Color.TRANSPARENT);
		background.setStroke(Color.TRANSPARENT);
		background.setStrokeWidth(2);
		return background;
	}

	private Text createDot(String string) {
		Text text = new Text(string);
		text.setFill(Color.web("#000000"));
		text.setFont(FONT);
		text.setTextOrigin(VPos.TOP);
		text.setLayoutX(1);
		text.setLayoutY(-4);
		return text;
	}

	private Rectangle createBackground(Color stroke, Color fill) {
		Rectangle background = new Rectangle(14, 17, fill);
		background.setStroke(stroke);
		background.setStrokeWidth(2);
		background.setEffect(new Lighting());
		background.setCache(true);
		return background;
	}

	private Rectangle createHourBackground(Color stroke, Color fill) {
		Rectangle background = new Rectangle(20, 31, fill);
		background.setStroke(stroke);
		background.setStrokeWidth(3);
		background.setEffect(new Lighting());
		background.setCache(true);
		return background;
	}

	private void configureHbox() {
		hBox.getChildren().addAll(digitsGroup);
		hBox.setSpacing(1);
	}

	private void configureHourHbox() {
		hourBox.getChildren().addAll(hoursGroup);
		hourBox.setSpacing(1);
	}

	public void refreshDigits(String hours, String time) {
		for (int i = 0; i < this.hours.length; i++) {
			this.hours[i].setText(hours.substring(i, i + 1));
		}
		// expecting time in format
		// "xx:xx:xx"
		for (int i = 0; i < digits.length; i++) {
			digits[i].setText(time.substring(i, i + 1));
		}
	}
}
