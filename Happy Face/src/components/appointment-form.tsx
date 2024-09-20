import {useForm} from 'react-hook-form'
import {zodResolver} from '@hookform/resolvers/zod'
import * as z from 'zod'
import {format} from 'date-fns'
import {CalendarIcon} from 'lucide-react'
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Input} from "@/components/ui/input"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Separator} from "@/components/ui/separator"
import {Calendar} from "@/components/ui/calendar"
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover"
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form"

const formSchema = z.object ({
    date: z.date ({
        required_error: "Appointment date is required",
    }),
    time: z.string ({
        required_error: "Appointment time is required",
    }),
    doctor: z.string ({
        required_error: "Doctor selection is required",
    }),
    appointmentType: z.string ({
        required_error: "Appointment type is required",
    }),
    name: z.string ()
        .min (2, "Name must be at least 2 characters")
        .regex (/^[a-zA-Z]+(\s[a-zA-Z]+)+$/, "Name must contain at least two words, each with 2 or more letters"),
    email: z.string ()
        .email ("Invalid email address"),
    birthdate: z.date ({
        required_error: "Birthdate is required",
    }).max (new Date (), "Birthdate cannot be in the future"),
    insuranceProvider: z.string ()
        .min (2, "Insurance provider name must be at least 2 characters"),
    insuranceNumber: z.string ()
        .regex (/^\d{6,}$/, "Insurance number must contain at least 6 digits"),
})

type FormValues = z.infer<typeof formSchema>

export function AppointmentFormComponent() {
    const form = useForm<FormValues> ({
        resolver: zodResolver (formSchema),
        defaultValues: {
            date: undefined,
            time: "",
            doctor: "",
            appointmentType: "",
            name: "",
            email: "",
            birthdate: undefined,
            insuranceProvider: "",
            insuranceNumber: "",
        },
    })

    const onSubmit = (data: FormValues) => {
        console.log (data)
        // Here you would typically send the data to your backend
    }

    return (
        <Card className="w-full max-w-3xl mx-auto">
            <CardHeader>
                <CardTitle>Create Appointment</CardTitle>
            </CardHeader>
            <CardContent>
                <Form {...form}>
                    <form onSubmit={(e) => void form.handleSubmit (onSubmit) (e)} className="space-y-8">
                        <div className="space-y-4">
                            <h2 className="text-lg font-semibold">Appointment Details</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="date"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Date</FormLabel>
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant={"outline"}
                                                            /* eslint-disable-next-line @typescript-eslint/no-unnecessary-condition,@typescript-eslint/restrict-template-expressions */
                                                            className={`w-full justify-start text-left font-normal ${!field.value && "text-muted-foreground"}`}
                                                        >
                                                            <CalendarIcon className="mr-2 h-4 w-4"/>
                                                            {(field.value as Date | undefined) ? format (field.value, "PPP") : "Pick a date"}
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar
                                                        mode="single"
                                                        selected={field.value}
                                                        onSelect={field.onChange}
                                                        disabled={(date) =>
                                                            date < new Date () || date > new Date (new Date ().setMonth (new Date ().getMonth () + 3))
                                                        }
                                                        initialFocus
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="time"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Time</FormLabel>
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select time"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="09:00">09:00 AM</SelectItem>
                                                    <SelectItem value="10:00">10:00 AM</SelectItem>
                                                    <SelectItem value="11:00">11:00 AM</SelectItem>
                                                    <SelectItem value="14:00">02:00 PM</SelectItem>
                                                    <SelectItem value="15:00">03:00 PM</SelectItem>
                                                    <SelectItem value="16:00">04:00 PM</SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="doctor"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Doctor</FormLabel>
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select doctor"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="dr-smith">Dr. Smith</SelectItem>
                                                    <SelectItem value="dr-johnson">Dr. Johnson</SelectItem>
                                                    <SelectItem value="dr-williams">Dr. Williams</SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="appointmentType"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Appointment Type</FormLabel>
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select type"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="check-up">Check-up</SelectItem>
                                                    <SelectItem value="consultation">Consultation</SelectItem>
                                                    <SelectItem value="follow-up">Follow-up</SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                            </div>
                        </div>
                        <Separator/>
                        <div className="space-y-4">
                            <h2 className="text-lg font-semibold">Patient Details</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="name"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Full Name</FormLabel>
                                            <FormControl>
                                                <Input placeholder="John Doe" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="birthdate"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Birthdate</FormLabel>
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant={"outline"}
                                                            /* eslint-disable-next-line @typescript-eslint/no-unnecessary-condition,@typescript-eslint/restrict-template-expressions */
                                                            className={`w-full justify-start text-left font-normal ${!field.value && "text-muted-foreground"}`}
                                                        >
                                                            <CalendarIcon className="mr-2 h-4 w-4"/>
                                                            {(field.value as Date | undefined) ? format (field.value, "PPP") :
                                                                <span>Pick a date</span>}
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar
                                                        mode="single"
                                                        selected={field.value}
                                                        onSelect={field.onChange}
                                                        disabled={(date) => date > new Date ()}
                                                        initialFocus
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="email"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Email</FormLabel>
                                            <FormControl>
                                                <Input type="email" placeholder="john@example.com" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="insuranceNumber"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Insurance Number</FormLabel>
                                            <FormControl>
                                                <Input placeholder="Insurance number" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="insuranceProvider"
                                    render={({field}) => (
                                        <FormItem className="md:col-span-2">
                                            <FormLabel>Insurance Provider</FormLabel>
                                            <FormControl>
                                                <Input placeholder="Insurance Provider Name" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                            </div>
                        </div>
                        <Button type="submit" className="w-full">Create Appointment</Button>
                    </form>
                </Form>
            </CardContent>
        </Card>
    )
}