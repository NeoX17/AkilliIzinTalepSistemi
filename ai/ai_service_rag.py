from fastapi import FastAPI
from pydantic import BaseModel
import openai  # OpenAI artık embeimport openaidding için kullanılmayacak
import faiss
import numpy as np
from docx import Document
import os
from dotenv import load_dotenv
from sentence_transformers import SentenceTransformer
import openai
import json
# .env'den anahtar oku (hem .env hem doğrudan anahtar desteği)
dotenv_path = os.path.join(os.path.dirname(__file__), '../.env')
load_dotenv(dotenv_path)
openai.api_key = os.getenv("OPENAI_API_KEY") or "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"
app = FastAPI()

# --- Word'den Kuralları Oku ---
def read_rules_from_word(file_path):
    doc = Document(file_path)
    rules = []
    for para in doc.paragraphs:
        text = para.text.strip()
        if text:
            rules.append(text)
    return rules

def read_rules_from_json(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    return [item['kural'] for item in data]

kurallar = read_rules_from_json("izin_politikalari.json")

# --- HuggingFace Sentence Transformers ile Embedding ---
embedder = SentenceTransformer('sentence-transformers/paraphrase-multilingual-mpnet-base-v2')

def get_embedding(text):
    emb = embedder.encode([text])[0]
    return emb.astype(np.float32)

print("Kurallar embedding'e hazırlanıyor...")
rule_embeddings = np.array([get_embedding(rule) for rule in kurallar])
print("Embedding işlemi tamamlandı.")
index = faiss.IndexFlatL2(rule_embeddings.shape[1])
index.add(rule_embeddings)

# --- Sorguya En Uygun Kuralı Bul ---
def find_relevant_rule(query, rules, index):
    query_emb = get_embedding(query)
    D, I = index.search(np.array([query_emb]), k=1)
    return rules[I[0][0]]

# --- AI Prompt'una Kuralı Ekle ve Sonucu Al ---
def get_ai_suggestion(leave_request, relevant_rule):
    prompt = (
        f"Şirket politikası: {relevant_rule}\n"
        f"Talep: {leave_request}\n"
        f"Lütfen sadece İK birimine hitap eden, kısa ve kesin bir öneri cümlesi kur. "
        f"Kurala göre bu talep onaylanmalı mı, reddedilmeli mi? Sadece 1 cümleyle, gerekçeli ve kesin bir şekilde yanıt ver."
    )
    print('Seçilen kural:', relevant_rule)
    print('AI prompt:', prompt)
    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}],
        max_tokens=100
    )
    return response.choices[0].message["content"]

# --- FastAPI Model ---
class LeaveRequest(BaseModel):
    employeeId: int
    firstName: str = ""
    lastName: str = ""
    startDate: str
    endDate: str
    department: str
    reason: str

@app.post("/analyze-leave")
async def analyze_leave(req: LeaveRequest):
    # Sorgu cümlesi oluştur
    query = f"{req.reason} {req.startDate} {req.endDate} {req.department}"
    # En uygun kuralı bul
    relevant_rule = find_relevant_rule(query, kurallar, index)
    # AI cevabını al
    leave_request_str = f"Çalışan: {req.firstName} {req.lastName}, Departman: {req.department}, {req.startDate}-{req.endDate} arası izin talep ediyor. Sebep: {req.reason}"
    ai_cevap = get_ai_suggestion(leave_request_str, relevant_rule)
    return {"result": ai_cevap} 