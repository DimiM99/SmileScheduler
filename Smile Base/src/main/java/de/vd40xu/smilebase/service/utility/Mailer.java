package de.vd40xu.smilebase.service.utility;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import de.vd40xu.smilebase.model.Appointment;
import jakarta.annotation.PostConstruct;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Generated
@Component
public class Mailer {

    @Value("${resend.api.key}")
    private String apiKey;

    private static Resend resend;

    @PostConstruct
    public void init() {
        resend = new Resend(apiKey);
    }

    public static void sendAppointmentConfirmation(Appointment appointment, boolean creating) throws MailerException {
        String subject = creating ? "Appointment Confirmation at Smile Scheduler" : "Appointment Rescheduled at Smile Scheduler";
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Smile Scheduler <no_reply@smilescheduler.de>")
                .to(appointment.getPatient().getEmail())
                .subject(subject)
                .html(customiseTheHtmlTemplate(appointment))
                .build();
        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new MailerException("Failed to send the email: " + e.getMessage());
        }
    }

    private static String customiseTheHtmlTemplate(Appointment appointment) {
        String mailTemplate = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Appointment Confirmation</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            text-align: center;
                            padding: 20px;
                            border-radius: 5px 5px 0 0;
                        }
                        .content {
                            background-color: #f9f9f9;
                            padding: 20px;
                            border-radius: 0 0 5px 5px;
                        }
                        .appointment-details {
                            background-color: white;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                            padding: 15px;
                            margin-top: 20px;
                        }
                        .button {
                            display: inline-block;
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-decoration: none;
                            border-radius: 5px;
                            margin-top: 20px;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 20px;
                            font-size: 0.8em;
                            color: #777;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>Appointment Confirmation</h1>
                    </div>
                    <div class="content">
                        <p>Dear {patientName},</p>
                        <p>Your appointment has been successfully scheduled. Here are the details:</p>
                
                        <div class="appointment-details">
                            <p><strong>Date:</strong> {appointmentDate}</p>
                            <p><strong>Time:</strong> {appointmentTime}</p>
                            <p><strong>Doctor:</strong> Dr. {doctorName}</p>
                            <p><strong>Type:</strong> {appointmentType}</p>
                        </div>
                
                        <p>If you need to reschedule or cancel your appointment, please contact us at least 24 hours in advance.</p>
                
                        <a href="{appointmentManagementLink}" class="button">Manage Your Appointments</a>
                
                        <p>Thank you for choosing our clinic. We look forward to seeing you!</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message. Please do not reply to this email.</p>
                        <p>&copy; 2024 Your Clinic Name. All rights reserved.</p>
                    </div>
                </body>
                </html>
                """;
        String managementLink = "http://localhost:8080/patient-schedule?pID=" + appointment.getPatient().getId();
        return mailTemplate
                .replace("{patientName}", appointment.getPatient().getName())
                .replace("{appointmentDate}", appointment.getStart().toLocalDate().toString())
                .replace("{appointmentTime}", appointment.getStart().toLocalTime().toString())
                .replace("{doctorName}", appointment.getDoctor().getName())
                .replace("{appointmentType}", appointment.getAppointmentType().toString())
                .replace("{appointmentManagementLink}", managementLink);
    }

    public static class MailerException extends RuntimeException {
        public MailerException(String message) {
            super(message);
        }
    }

}
