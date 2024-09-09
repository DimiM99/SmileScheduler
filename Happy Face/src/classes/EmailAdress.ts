export class EmailAddress {
    private constructor(private readonly value: string) {}

    static isValidEmail(email: string): boolean {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    static create(email: string): EmailAddress {
        if (!this.isValidEmail(email)) {
            throw new Error('Invalid email address format');
        }
        return new EmailAddress(email);
    }

    getValue(): string {
        return this.value;
    }
}