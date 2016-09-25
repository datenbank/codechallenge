
package codechallenge

import groovy.json.JsonSlurper
import java.text.DateFormat
import java.text.SimpleDateFormat

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

class TrustScore {

	def APIKEY = 'tiNFg9zDKRWXaIpI8pPyWbXd4ACa9ZDt'
	def reviews = []
	LambdaLogger logger

	def starToNum = [0, 2.5, 5, 7.5, 10]

	def int getDays(createdAt) {

		try {
			DateFormat format

			if(createdAt.length() == 20) {
				format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			} else {
				format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			}

			def created = format.parse(createdAt)
			def date = new Date()

			def days = 0

			use(groovy.time.TimeCategory) {
				def duration = date - created
				days = duration.days
			}
			return days
		} catch (all) {
			throw new Exception("Error parsing or calculating days since review.")
		}
	}

	def trustscore(reviews) {

		try {

			def sum = 0
			def div = 0

			def i = 0

			reviews.each {

				def stars = it[0]
				def days = getDays(it[1])
				def weight =  (365-days)

				if(weight > 0) {
					sum += (starToNum[stars-1] * weight)
					div += weight
					i++
				}
			}
			Double rate = sum / div

			def rateRound = rate.round(1)

			return rateRound
		} catch(all) {
			throw new Exception("Error calculating trustscore on reviews.")
		}
	}

	def getId(domain) {
		try {
			def slurper = new JsonSlurper()
			def json = "https://api.trustpilot.com/v1/business-units/find?name=${domain}".toURL().getText(requestProperties: [apikey: APIKEY])
			def result = slurper.parseText(json)
			return result.id
		} catch(all) {
			throw new Exception("Error finding id from domain: $domain")
		}
	}

	def getReviews(url) {

		logger?.log("Go get url: $url")

		try {
			def slurper = new JsonSlurper()

			def json = url.toURL().getText(requestProperties: [apikey: APIKEY])
			def resultReviews = slurper.parseText(json)

			resultReviews.reviews.each {
				reviews << [(int)it.stars, it.createdAt]
			}

			if(reviews.size() < 300) {
				resultReviews.links.each {
					if(it.rel=='next-page') {

						getReviews(it.href) //recursive
					}
				}
			}
		} catch(all) {
			throw new Exception("Error retrieving ratings for this business id: $id")
		}
	}

	def double calcTrustscore(String domain) {

		logger?.log("Start this request for domain: $domain")

		def id = getId(domain)

		logger?.log("Got id: $id")

		getReviews("https://api.trustpilot.com/v1/business-units/${id}/reviews?orderBy=createdAt.desc&perPage=100")

		def cnt = reviews.size()
		logger?.log("Got reviews: ${cnt}")

		if(reviews.size()==0) {
			throw new Exception("No reviews to base trustscore on for domain: $domain")
		}

		double score = trustscore(reviews)
		logger?.log("The score is: $score")


		return score
	}

	def double handler(Map<String,Object> input, Context context) {
		logger = context?.getLogger()

		def domain = input.domain

		if(domain == "")
			throw new Exception("Domain parameter not set!")

		def score = new TrustScore().calcTrustscore(domain)

		return score
	}

	def static main(args) {
		//test local
		println new TrustScore().handler([domain: 'trustpilot.com'], null)

		//test the service
		def slurper = new JsonSlurper()
		def json = "https://822qlg4tye.execute-api.us-west-2.amazonaws.com/prod/TrustPilotScore?domain=trustpilot.com".toURL().getText()
		def result = slurper.parseText(json)
		println result.TrustScore


	}
}
