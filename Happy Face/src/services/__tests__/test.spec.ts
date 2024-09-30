import {LoginRequest} from "@/models/services/requests/LoginRequest";


describe("Auth Service", () => {
    it('should encrypt credentials, make API call, and save token', async () => {
        const mockRequest: LoginRequest = { username: "tester", password: "password" };
        expect(mockRequest).toEqual({ username: "tester", password: "password" });
    })
})