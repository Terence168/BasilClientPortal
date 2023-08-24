import { DateTime } from "luxon";

/**
 * Parse ISO format date
 */
export const parseDate = (timeStr) => {
    if(timeStr === null || timeStr === ""){
      return "N/A"
    }
    const timeFormat = 'yyyy-LL-dd';
    const time = DateTime.fromISO(timeStr);
    return time.toFormat(timeFormat);
}

export const parseDateTime = (timeStr)=> {
    if(timeStr === null || timeStr === ""){
      return "N/A"
    }
    const timeFormat = 'yyyy-LL-dd tt';
    const time = DateTime.fromISO(timeStr);
    return time.toFormat(timeFormat);
}