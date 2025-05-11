from fastapi import FastAPI
from pydantic import BaseModel
import openai
import os

app = FastAPI()

openai.api_key = os.getenv("OPENAI_API_KEY", "sk-...buraya_anahtarini_koy...")

class LeaveRequest(BaseModel):
    employeeId: int
    startDate: str
    endDate: str
    department: str
    reason: str

@app.post("/analyze-leave")
async def analyze_leave(req: LeaveRequest):
    prompt = (
        f"Bir çalışan izin talebinde bulunuyor.\n"
        f"Çalışan ID: {req.employeeId}\n"
        f"Departman: {req.department}\n"
        f"Başlangıç: {req.startDate}\n"
        f"Bitiş: {req.endDate}\n"
        f"Neden: {req.reason}\n"
        f"Lütfen şirket izin politikalarına göre bu talebin uygun olup olmadığını, gerekçesiyle birlikte açıkla. "
        f"Uygun değilse alternatif tarih önerisi de sun."
    )
    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}],
        max_tokens=256
    )
    answer = response.choices[0].message.content
    return {"result": answer} 