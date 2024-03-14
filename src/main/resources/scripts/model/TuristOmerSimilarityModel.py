import spacy

nlp = spacy.load('en_core_web_lg')

cuisine_list_txt_path = r'D:\TOBB\Bil496\scripts\model\word-to-vec\cuisine_list_en.txt'
hotel_amenity_list_txt_path = r'D:\TOBB\Bil496\scripts\model\word-to-vec\all_amenities.txt'

with open(cuisine_list_txt_path, "r", encoding='utf-8') as file:
    cuisine_list = [line.strip() for line in file.readlines()]

with open(hotel_amenity_list_txt_path, "r", encoding='utf-8') as file:
    amenity_list = [line.strip() for line in file.readlines()]


def get_similarities(entity, threshold=0.7):
    entity_type = entity['type']
    
    if(entity_type == 'Cuisine' or entity_type == 'Dish'):
        return get_similarity_for_cuisine(entity,threshold)
    
    if(entity_type == 'Location'):
        return get_similarity_for_location(entity,threshold)
    
    if(entity_type == 'Price'):
        return get_similarity_for_price(entity,threshold=0.9)

    if(entity_type == 'Hours'):
        return get_similarity_for_meal(entity, threshold=0.9)
    
    if(entity_type == 'Amenity'):
        return get_similarity_for_amenity(entity, is_restaurant=True, threshold=0.6)

    
def get_similarity_for_cuisine(entity, threshold=0.7):
    similar_cuisines = []

    cuisine_str = ' '.join(entity['entity'])
    similar_cuisines.append(cuisine_str)

    cuisine_name = nlp(cuisine_str.lower())
    similarities = [cuisine_name.similarity(nlp(word.lower())) for word in cuisine_list]

    for word, similarity in zip(cuisine_list, similarities):
        if similarity >= threshold:
            similar_cuisines.append(word)

    return {"cuisine": similar_cuisines}


def get_similarity_for_location(entity, threshold=0.7):
    is_close = 0
    loc_str = ' '.join(entity['entity'])
    loc_name = nlp(loc_str.lower())

    near_list = ["nearby", "near", "close", "neighboring", "around me", "close to my current location", "in area"]
    similarities = [loc_name.similarity(nlp(loc.lower())) for loc in near_list]

    for similarity in similarities:
        if similarity >= threshold:
            is_close = 1
    
    return {"location": str(loc_name), "is_close": is_close}


def get_similarity_for_price(entity, threshold=0.9):
    is_cheap = 0
    is_expensive = 0

    price_str = ' '.join(entity['entity'])
    price_name = nlp(price_str.lower())

    cheap_list = ["cheap", "economical", "affordable" ,"low cost", "low priced"]
    expensive_list = ["expensive", "luxurious", "costly", "high cost", "overpriced"]

    cheap_similarities = [price_name.similarity(nlp(price.lower())) for price in cheap_list]
    expensive_similarities = [price_name.similarity(nlp(price.lower())) for price in expensive_list]

    for similarity in cheap_similarities:
        if similarity >= threshold:
            is_cheap = 1
    
    for similarity in expensive_similarities:
        if similarity >= threshold:
            is_expensive = 1
    
    return {"price": str(price_str), "is_cheap": is_cheap, "is_expensive": is_expensive}


def get_similarity_for_meal(entity, threshold=0.9):
    similar_meals = []

    meal_str = ' '.join(entity['entity'])
    meal_name = nlp(meal_str.lower())

    meal_list = ["breakfast","dinner","lunch","brunch"]
    similarities = [meal_name.similarity(nlp(meal.lower())) for meal in meal_list]

    for meal, similarity in zip(meal_list, similarities):
        if similarity >= threshold:
            similar_meals.append(meal)

    return {"meal": similar_meals}


def get_similarity_for_amenity(entity,is_restaurant,threshold=0.9):
    similar_amenities = []

    amenity_str = ' '.join(entity['entity'])
    amenity_name = nlp(amenity_str.lower())

    if(is_restaurant):
        amenities = ["big group","romantic"]
    else:
        amenities = amenity_list
    
    similarities = [amenity_name.similarity(nlp(amenity.lower())) for amenity in amenities]

    for amenity, similarity in zip(amenities, similarities):
        if similarity >= threshold:
            similar_amenities.append(amenity)

    return {"amenity": similar_amenities}


