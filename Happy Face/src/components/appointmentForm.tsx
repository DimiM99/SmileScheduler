import {useForm} from 'react-hook-form'
import {zodResolver} from '@hookform/resolvers/zod'
import * as z from 'zod'
import {addMinutes, format} from 'date-fns'
import {CalendarIcon} from 'lucide-react'
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Input} from "@/components/ui/input"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Calendar} from "@/components/ui/calendar"
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover"
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/Form";
import {useEffect} from "react";
import {AppointmentResponse} from "@/models/services/responses/AppointmentResponse.ts";
import {Doctor} from "@/models";
import {appointmentDurations, AppointmentType} from "@/models/enums/AppointmentType.ts";
import {Separator} from "@/components/ui/separator.tsx";
import {useAppointmentStore} from "@/hooks/zustand/useAppointmentStore.ts";

const formSchema = z.object({
    date: z.date({
        required_error: "Appointment date is required",
    }),
    startTime: z.string({
        required_error: "Start time is required",
    }),
    endTime: z.string({
        required_error: "End time is required",
    }),
    appointmentType: z.nativeEnum(AppointmentType),
    doctorId: z.number({
        required_error: "Doctor selection is required",
    }),
    patientId: z.number().optional(),
    patientName: z.string().min(2, "Name must be at least 2 characters"),
    patientEmail: z.string().email("Invalid email address"),
    patientBirthdate: z.date({
        required_error: "Birthdate is required",
    }),
    patientInsuranceProvider: z.string().min(2, "Insurance provider name must be at least 2 characters"),
    patientInsuranceNumber: z.string().regex(/^\d{6,}$/, "Insurance number must contain at least 6 digits"),
    patientPhoneNumber: z.string().regex(/^\+?[1-9]\d{1,14}$/, "Invalid phone number"),
});

export type FormValues = z.infer<typeof formSchema>;

interface AppointmentFormProps {
    selectedAppointment: AppointmentResponse | null;
    doctors: Doctor[];
    onSubmit: (data: FormValues, isCreate: boolean) => void;
    currentlySelectedDoctor: Doctor | null;
    dropSelectedAppointment: () => void;
    newDoctorSelected: (docktor: Doctor) => void;
    currentDateChange: (date: Date) => Promise<void>;
}

export function AppointmentForm({
                                    selectedAppointment,
                                    doctors,
                                    onSubmit,
                                    currentlySelectedDoctor,
                                    dropSelectedAppointment,
                                    newDoctorSelected,
                                    currentDateChange
}: AppointmentFormProps) {

    const {availableSlots, fetchAvailableSlots} = useAppointmentStore();

    const form = useForm<FormValues>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            date: new Date(),
            startTime: "",
            endTime: "",
            appointmentType: AppointmentType.EXTENSIVE,
            doctorId: currentlySelectedDoctor?.id || 0,
            patientName: "",
            patientEmail: "",
            patientBirthdate: new Date(),
            patientInsuranceProvider: "",
            patientInsuranceNumber: "",
            patientPhoneNumber: "",
        },
    });

    const updateAvailableSlots = (doctorId: number) => {
        const date = form.getValues('date');
        const appointmentType = form.getValues('appointmentType');
        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        if (date && appointmentType) {
            console.log('Updating available slots', doctorId, date, appointmentType);
            // eslint-disable-next-line @typescript-eslint/no-floating-promises
            fetchAvailableSlots(doctorId, date, appointmentType);
        }
    };

    const parseAppointmentType = (type: string) => {
        switch (type) {
            case 'QUICKCHECK':
                return 'Quick Check';
            case 'EXTENSIVE':
                return 'Extensive';
            case 'SURGERY':
                return 'Surgery';
            default:
                return 'Unknown';
        }
    }

    const constDateChange = (date: Date) => {
        form.setValue('date', date);
        // eslint-disable-next-line @typescript-eslint/no-floating-promises
        currentDateChange(date);
        updateAvailableSlots(form.getValues('doctorId'));
    }

    const handleSubmit = (data: FormValues) => {
        console.log(data);
        onSubmit(data, !selectedAppointment);
        form.reset();
    }

    useEffect(() => {
        if (selectedAppointment) {
            try {
                form.setValue('date', new Date(selectedAppointment.start));
                form.setValue('startTime', selectedAppointment.start);
                form.setValue('endTime', selectedAppointment.end);
                form.setValue('appointmentType', selectedAppointment.appointmentType);
                form.setValue('doctorId', selectedAppointment.doctor.id);
                form.setValue('patientId', selectedAppointment.patient.id);
                form.setValue('patientName', selectedAppointment.patient.name);
                form.setValue('patientEmail', selectedAppointment.patient.email);
                form.setValue('patientBirthdate', new Date(selectedAppointment.patient.birthdate));
                form.setValue('patientInsuranceProvider', selectedAppointment.patient.insuranceProvider);
                form.setValue('patientInsuranceNumber', selectedAppointment.patient.insuranceNumber);
                form.setValue('patientPhoneNumber', selectedAppointment.patient.phoneNumber);
                updateAvailableSlots(selectedAppointment.doctor.id);
            } catch (error) {
                console.error('Error parsing appointment:', error);
            }
        }
    }, [selectedAppointment]);

    function resetFrom() {
        dropSelectedAppointment();
        form.reset({
            date: new Date(),
            startTime: "",
            endTime: "",
            appointmentType: AppointmentType.EXTENSIVE,
            doctorId: currentlySelectedDoctor?.id || 0,
            patientName: "",
            patientEmail: "",
            patientBirthdate: new Date(),
            patientInsuranceProvider: "",
            patientInsuranceNumber: "",
            patientPhoneNumber: "",
        });
    }

    return (
        <Card className="w-full max-w-3xl mx-auto">
            <CardHeader>
                <CardTitle
                    className="text-lg font-semibold">{selectedAppointment ? 'Edit Appointment' : 'Create Appointment'}</CardTitle>
            </CardHeader>
            <CardContent>
                <Form {...form}>
                    {/* eslint-disable-next-line @typescript-eslint/no-misused-promises */}
                    <form onSubmit={form.handleSubmit((e) => {
                        handleSubmit(e)
                    }, (errors, event) => {
                        console.log("hui ", errors, event)
                    })} className="space-y-8">
                        <div className="space-y-4">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="date"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Date
                                                <FormMessage/>
                                            </FormLabel>
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button variant="outline"
                                                            /* eslint-disable-next-line @typescript-eslint/no-unnecessary-condition,@typescript-eslint/restrict-template-expressions */
                                                                className={`w-full justify-start text-left font-normal ${!field.value && "text-muted-foreground"}`}>
                                                            <CalendarIcon className="mr-2 h-4 w-4"/>
                                                            {(field.value as Date | undefined) ? format(field.value, "PPP") :
                                                                <span>Pick a date</span>}
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar
                                                        mode="single"
                                                        selected={field.value}
                                                        //eslint-disable-next-line @typescript-eslint/ban-ts-comment
                                                        onSelect={(date) => { // @ts-expect-error
                                                            constDateChange(date);
                                                        }}
                                                        disabled={(date) => date < new Date()}
                                                        initialFocus
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="startTime"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Start Time
                                                <FormMessage/>
                                            </FormLabel>
                                            <Select onValueChange={field.onChange} value={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select start time"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    {
                                                        selectedAppointment && (
                                                            <SelectItem key={selectedAppointment.start} value={selectedAppointment.start}>
                                                                {format(selectedAppointment.start, 'HH:mm')}
                                                            </SelectItem>
                                                        )
                                                    }
                                                    {availableSlots.map((slot) => (
                                                        <SelectItem key={slot} value={slot}>
                                                            {format(new Date(slot), 'HH:mm')}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="endTime"
                                    render={() => (
                                        <FormItem>
                                            <FormLabel>
                                                End Time
                                                <FormMessage/>
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    disabled={true}
                                                    type="time"
                                                    value={
                                                        form.watch('startTime') ?
                                                            format(
                                                                addMinutes(
                                                                    form.getValues('startTime'),
                                                                    appointmentDurations[form.watch('appointmentType')]
                                                                ),
                                                                'HH:mm'
                                                            ) : ''
                                                    }
                                                />
                                            </FormControl>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="appointmentType"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Appointment Type
                                                <FormMessage/>
                                            </FormLabel>
                                            <Select onValueChange={
                                                    (value) => {
                                                        field.onChange(value);
                                                        updateAvailableSlots(form.getValues('doctorId'));
                                                    }
                                                }
                                                value={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select type"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    {Object.values(AppointmentType).map((type) => (
                                                        <SelectItem key={type} value={type}>
                                                            {parseAppointmentType(type)}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="doctorId"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Doctor
                                                <FormMessage/>
                                            </FormLabel>
                                            <Select
                                                onValueChange={(value) => {
                                                    field.onChange(parseInt(value));
                                                    newDoctorSelected(doctors.find((doctor) => doctor.id === parseInt(value)) as Doctor);
                                                }}
                                                value={field.value.toString()}
                                            >
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select doctor"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    {doctors.map((doctor) => (
                                                        <SelectItem key={doctor.id} value={doctor.id.toString()}>
                                                            {doctor.name}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </FormItem>
                                    )}
                                />
                            </div>
                        </div>

                        <Separator/>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="patientName"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Patient Name
                                                <FormMessage/>
                                            </FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="patientEmail"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Patient Email
                                                <FormMessage/>
                                            </FormLabel>
                                            <FormControl>
                                                <Input type="email" {...field} />
                                            </FormControl>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="patientBirthdate"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Patient Birthdate
                                                <FormMessage/>
                                            </FormLabel>
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button variant="outline"
                                                            /* eslint-disable-next-line @typescript-eslint/no-unnecessary-condition,@typescript-eslint/restrict-template-expressions */
                                                                className={`w-full justify-start text-left font-normal ${!field.value && "text-muted-foreground"}`}>
                                                            <CalendarIcon className="mr-2 h-4 w-4"/>
                                                            {(field.value as Date | undefined) ? format(field.value, "PPP") :
                                                                <span>Pick a date</span>}
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar
                                                        mode="single"
                                                        selected={field.value}
                                                        onSelect={field.onChange}
                                                        disabled={(date) => date > new Date()}
                                                        initialFocus
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="patientInsuranceProvider"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Insurance Provider
                                                <FormMessage/>
                                            </FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="patientInsuranceNumber"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Insurance Number
                                                <FormMessage/>
                                            </FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="patientPhoneNumber"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>
                                                Phone Number
                                                <FormMessage/>
                                            </FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                        </FormItem>
                                    )}
                                />
                            </div>
                        </div>
                        <div className="flex flex-row m-1">
                            <Button type="submit" className="w-full">
                                {!selectedAppointment ? 'Create Appointment' : 'Update Appointment'}
                            </Button>
                            <Button variant="secondary" type="reset" className="w-full" onClick={resetFrom}>
                                Cancel
                            </Button>
                        </div>
                    </form>
                </Form>
            </CardContent>
        </Card>
    );
}