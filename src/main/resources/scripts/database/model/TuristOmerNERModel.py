from tner import TransformersNER
from fastapi import FastAPI
from typing import List, Tuple, Dict, Any
from pydantic import BaseModel

model_name = "tner/roberta-large-mit-restaurant"
model = TransformersNER(model_name)
app = FastAPI()

class PromptInput(BaseModel):
    prompt: str

class EntityPrediction(BaseModel):
    type: str
    entity: List[str]
    position: List[int]
    probability: List[float]

@app.post("/predict/", response_model=List[EntityPrediction])
async def predict(input_prompt: PromptInput):
    text = input_prompt.prompt
    prediction = model.predict([text])
    return prediction['entity_prediction'][0]
