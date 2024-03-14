from tner import TransformersNER
from fastapi import FastAPI
from typing import List, Optional
from pydantic import BaseModel
import uvicorn
import TuristOmerSimilarityModel as sim

class PromptInput(BaseModel):
    prompt: str

class EntityPrediction(BaseModel):
    type: str
    entity: List[str]
    position: List[int]
    probability: List[float]

class EntityResponseModel(BaseModel):
    cuisine: Optional[List[str]]
    location: Optional[List[str]]
    meal: Optional[List[str]]
    isClose: Optional[List[int]]
    price: Optional[List[str]]
    isCheap: Optional[List[int]]
    isExpensive: Optional[List[int]]
    amenity: Optional[List[str]]
    rating: Optional[List[str]]

model_name = "tner/roberta-large-mit-restaurant"
model = TransformersNER(model_name)
app = FastAPI()

@app.post("/predict", response_model=EntityResponseModel)
async def predict(input_prompt: PromptInput):
    prompt_text = input_prompt.prompt
    prediction = get_prediction(prompt_text)
    similarity_dict = get_similar_by_category(prediction)

    response_data = {
        "cuisine": similarity_dict.get("cuisine"),
        "location": similarity_dict.get("location"),
        "meal": similarity_dict.get("meal"),
        "isClose": similarity_dict.get("is_close"),
        "price": similarity_dict.get("price"),
        "isCheap": similarity_dict.get("is_cheap"),
        "isExpensive": similarity_dict.get("is_expensive"),
        "amenity": similarity_dict.get("amenity"),
        "rating": similarity_dict.get("rating")
    }

    print(response_data)

    return EntityResponseModel(**response_data)


def get_prediction(prompt:str):
    prediction = model.predict([prompt])
    return prediction['entity_prediction'][0]


def get_similar_by_category(prediction: List[EntityPrediction]):
    similarities = {}
    for entity in prediction:
        print(entity)
        curr_entity_dict = sim.get_similarities(entity)
        if curr_entity_dict is not None:
            similarities = merge_dicts(similarities, curr_entity_dict)

    return similarities


def merge_dicts(dict1, dict2):
    merged_dict = dict(dict1)
    for key, value in dict2.items():
        if key in merged_dict:
            if isinstance(value, list):
                merged_dict[key].extend(value)
            else:
                merged_dict[key].append(value)
        else:
            if isinstance(value, list):
                merged_dict[key] = value[:]
            else:
                merged_dict[key] = [value]
    return merged_dict


if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
