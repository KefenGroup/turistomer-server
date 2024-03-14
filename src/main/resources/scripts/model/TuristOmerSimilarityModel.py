import spacy

nlp = spacy.load('en_core_web_lg')

cuisine_list_txt_path = r'D:\TOBB\Bil496\scripts\model\word-to-vec\cuisine_list_en.txt'
hotel_amenity_list_txt_path = r'D:\TOBB\Bil496\scripts\model\word-to-vec\all_amenities.txt'

with open(cuisine_list_txt_path, "r", encoding='utf-8') as file:
    cuisine_list = [line.strip() for line in file]

with open(hotel_amenity_list_txt_path, "r", encoding='utf-8') as file:
    amenity_list = [line.strip() for line in file]

near_list = ["nearby", "near", "close", "neighboring", "around me", "close to my current location", "in area"]
cheap_list = ["cheap", "economical", "affordable" ,"low cost", "low priced"]
expensive_list = ["expensive", "luxurious", "costly", "high cost", "overpriced"]
meal_list = ["breakfast", "dinner", "lunch", "brunch"]

def get_similarities(entity, threshold=0.7):
    entity_type = entity['type']
    if entity_type in ['Cuisine', 'Dish']:
        return get_similarity_for_cuisine(entity, threshold)
    elif entity_type == 'Location':
        return get_similarity_for_location(entity, threshold)
    elif entity_type == 'Price':
        return get_similarity_for_price(entity, threshold=0.9)
    elif entity_type == 'Hours':
        return get_similarity_for_meal(entity, threshold=0.9)
    elif entity_type == 'Amenity':
        return get_similarity_for_amenity(entity, is_restaurant=True, threshold=0.6)
    elif entity_type == 'Rating':
        return {"rating": ' '.join(entity['entity'])}

def get_similarity_for_cuisine(entity, threshold=0.7):
    cuisine_str = ' '.join(entity['entity'])
    similar_cuisines = [cuisine_str] + [word for word in cuisine_list if nlp(cuisine_str.lower()).similarity(nlp(word.lower())) >= threshold]
    return {"cuisine": similar_cuisines}

def get_similarity_for_location(entity, threshold=0.7):
    loc_str = ' '.join(entity['entity'])
    loc_name = nlp(loc_str.lower())
    is_close = any(loc_name.similarity(nlp(loc.lower())) >= threshold for loc in near_list)
    return {"location": str(loc_name), "is_close": int(is_close)}

def get_similarity_for_price(entity, threshold=0.9):
    price_str = ' '.join(entity['entity'])
    price_name = nlp(price_str.lower())
    is_cheap = any(price_name.similarity(nlp(price.lower())) >= threshold for price in cheap_list)
    is_expensive = any(price_name.similarity(nlp(price.lower())) >= threshold for price in expensive_list)
    return {"price": str(price_str), "is_cheap": int(is_cheap), "is_expensive": int(is_expensive)}

def get_similarity_for_meal(entity, threshold=0.9):
    meal_str = ' '.join(entity['entity'])
    similar_meals = [meal for meal in meal_list if nlp(meal_str.lower()).similarity(nlp(meal.lower())) >= threshold]
    return {"meal": similar_meals}

def get_similarity_for_amenity(entity, is_restaurant, threshold=0.9):
    amenity_str = ' '.join(entity['entity'])
    amenity_name = nlp(amenity_str.lower())
    amenities = amenity_list if not is_restaurant else ["big group", "romantic"]
    similar_amenities = [amenity for amenity in amenities if amenity_name.similarity(nlp(amenity.lower())) >= threshold]
    return {"amenity": similar_amenities}
