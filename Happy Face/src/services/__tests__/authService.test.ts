import {AuthService} from "src/services/authService";
import CryptoJS from "crypto-js";
import {apiWithoutAuth} from "src/services/apiConfig";
import {LoginRequest} from "src/models/services/requests/LoginRequest";
import {LoginResponse} from "src/models/services/responses/LoginResponse";

jest.mock('crypto-js');

class SessionStorageMock {
    private store: Record<string, string> = {};

    clear(): void {
        this.store = {};
    }

    getItem(key: string): string | null {
        return this.store[key] ?? null;
    }

    setItem(key: string, value: string): void {
        this.store[key] = String(value);
    }

    removeItem(key: string): void {
        // eslint-disable-next-line @typescript-eslint/no-dynamic-delete
        delete this.store[key];
    }
}

const mockSessionStorage = new SessionStorageMock();
Object.defineProperty(global, 'sessionStorage', { value: mockSessionStorage });

describe("Auth Service", () => {
    let authService: AuthService;

    beforeEach(() => {
        authService = new AuthService();
        jest.clearAllMocks();
        mockSessionStorage.clear();
    });

    describe('login', () => {
        it('should encrypt credentials, make API call, and save token', async () => {
            const mockRequest: LoginRequest = { username: "testuser", password: "password" };
            const mockResponse: LoginResponse = {
                token: "mockToken",
                expiresIn: 3600
            };


            const encryptSpy = jest.spyOn(CryptoJS.AES, 'encrypt');
            const setItemSpy = jest.spyOn(mockSessionStorage, 'setItem');
            const apiPostSpy = jest.spyOn(apiWithoutAuth, 'post');

            await authService.login(mockRequest);

            expect(encryptSpy).toHaveBeenCalledWith(JSON.stringify(mockRequest), expect.any(String));
            expect(setItemSpy).toHaveBeenCalledWith('credentials', "encryptedCreds");
            expect(apiPostSpy).toHaveBeenCalledWith('/auth/login', mockRequest);
            expect(apiPostSpy).toHaveReturnedWith(Promise.resolve({ data: mockResponse }));
            expect(setItemSpy).toHaveBeenCalledWith('token', expect.any(String));
            expect(setItemSpy).toHaveBeenCalledWith('tokenExpiry', expect.any(String));
        });
    });
});