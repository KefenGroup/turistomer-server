from tner import TransformersNER
from fastapi import FastAPI
from typing import List
from pydantic import BaseModel
import uvicorn

class PromptInput(BaseModel):
    prompt: str

class EntityPrediction(BaseModel):
    type: str
    entity: List[str]
    position: List[int]
    probability: List[float]


model_name = "tner/roberta-large-mit-restaurant"
model = TransformersNER(model_name)
app = FastAPI()

@app.post("/predict/", response_model=List[EntityPrediction])
async def predict(input_prompt: PromptInput):
    text = input_prompt.prompt
    prediction = model.predict([text])
    return prediction['entity_prediction'][0]

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
