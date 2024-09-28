import {clsx, type ClassValue} from "clsx"
import {twMerge} from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
    return twMerge (clsx (inputs))
}

export function handleAsyncErrors<A extends unknown[]>(p: (...args: A) => Promise<void>): (...args: A) => void {
  return (...args: A) => {
    try {
      p(...args).catch((err: unknown) => { console.log("Error thrown asynchronously", err); })
    } catch (err) {
      console.log("Error thrown synchronously", err)
    }
  }
}