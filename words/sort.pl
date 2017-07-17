

use 5.010;
use strict;
use warnings;
use utf8;
use Cwd;
#use DateTime;
use POSIX;

my @saveWords;
my @saveWordsWithFrequency;

my $count = 0;
open my $infile, '<', "google-books-common-words.txt";
while (<$infile>){	
	my $line = $_;
	chomp($line);
	if ($line =~ m/^([A-Z]{3,9})\s+(\d+)/){
		push @saveWords, $1;
		push @saveWordsWithFrequency, "$1 $2";
	}

	# if ($count == 10){
	# 	last;
	# }
	$count++;
}


close $infile;
open my $outfile, '>', "google-books-common-words_filtered.txt";
foreach my $word (@saveWords){
	print $outfile "$word\n";
}
close $outfile;
open $outfile, '>', "google-books-common-words_filtered-With-Frequency.txt";
foreach my $word (@saveWordsWithFrequency){
	print $outfile "$word\n";
}
close $outfile;
